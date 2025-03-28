package edu.uob;

import edu.uob.action.CustomAction;
import edu.uob.action.GameAction;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

import java.util.*;

public class Tokeniser {

    String command;
    GameState state;
    Map<String, Location> locations;
    Player player;
    List<String> tokens;

    public Tokeniser(GameState state, Map<String, Location> locations, Player player, String command) {
        this.command = this.normaliseCommand(command);
        this.state = state;
        this.locations = locations;
        this.player = player;
        tokens = new LinkedList<>();
        Scanner scanner = new Scanner(this.command);
        scanner.useDelimiter(" ");

        while (scanner.hasNext()) {
            tokens.add(scanner.next());
        }
        scanner.close();
    }

    public EntityList getEntities() throws Exception {

        Set<GameEntity> entitySet = new HashSet<>();

        for (String entityName : tokens) {
            GameEntity entity = GameState.getEntityFromLocations(entityName, locations);

            if (entity == null) { // entity may be artefact in player inventory
                entity = player.getEntityFromInventory(entityName);
            }
            if (entity == null && (entity = this.getEntityFromAllInventories(entityName)) != null) {
                throw new STAGException.EntityInAnotherPlayersInventoryException(entity);
            }
            if (entity == null) continue; // token is not an entity

            if (!entitySet.add(entity)) { // entity is given more than once
                throw new STAGException.DuplicateEntityInCommandException(entity);
            }
        }
        return new EntityList(entitySet);
    }

    private GameEntity getEntityFromAllInventories(String entityName) {

        GamePlayers players = state.getPlayers();

        for (Player player : players) {
            GameEntity entity = player.getEntityFromInventory(entityName);

            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public GameAction getAction(EntityList entities) throws Exception {

        // holds all actions that use any key phrase present in command
        Set<GameAction> actions = new HashSet<>();

        for (String keyPhrase : tokens) {
            // hold all actions that use specific key phrase
            Set<GameAction> actionSet = state.getAction(keyPhrase);
            if (actionSet == null) continue; // token is not key phrase
            GameAction action = null;

            // if action set size > 1, determine which is the relevant action from the subjects
            if (actionSet.size() > 1) {
                action = this.getActionFromEntities(actionSet, entities);
                if (action == null) continue; // cant match an action with entities
            }
            else action = actionSet.iterator().next();

            if (!actions.add(action)) { // key phrase is used more than once
                throw new STAGException.DuplicateKeyPhraseInCommandException();
            }
        }
        if (actions.isEmpty()) { // keyPhrase may contain multiple words
            GameAction action = this.getMultiWordAction(entities);
            if (action != null) actions.add(action);
        }
        if (actions.isEmpty()) { // no action is specified
            throw new STAGException.NoActionFoundException();
        }
        if (actions.size() > 1) { // ambiguous command, multiple key phrases
            throw new STAGException.AmbiguousCommandException();
        }
        return actions.iterator().next();
    }

    public GameAction getActionFromEntities(Set<GameAction> actions, EntityList entities) {

        GameAction intendedAction = null;
        int currentMatches = 0;

        for (GameAction action : actions) { //TODO: assume actions with same trigger phrase are CustomActions?
            EntityList subjects = ((CustomAction) action).getSubjects();
            int matches = 0;

            for (GameEntity entity : entities) {
                if (subjects.containsEntity(entity)) matches++;
            }
            if (matches > currentMatches) {
                intendedAction = action;
                currentMatches = matches;
            }
            else if (matches == currentMatches) {
                intendedAction = null;
            }
        }
        return intendedAction;
    }

    private GameAction getMultiWordAction(EntityList entities) {

        GameAction action = null;

        for (Map.Entry<String, Set<GameAction>> entry : state.getActions().entrySet()) {

            if (command.contains(entry.getKey())) {
                Set<GameAction> actionSet = entry.getValue();

                if (actionSet.size() > 1) {
                    action = this.getActionFromEntities(actionSet, entities);
                }
                else {
                    action = actionSet.iterator().next();
                }
            }
        }
        return action;
    }

    private String normaliseCommand(String command) {

        command = this.replaceChars(command, "  ", " ");
        command = this.replaceChars(command, ".", "");
        command = this.replaceChars(command, ",", "");
        command = this.replaceChars(command, ":", "");
        command = this.replaceChars(command, ";", "");
        command = this.replaceChars(command, "!", "");
        command = this.replaceChars(command, "?", "");
        return command.toLowerCase();
    }

    private String replaceChars(String command, String target, String replacement) {

        while (command.contains(target)) {
            command = command.replace(target, replacement);
        }
        return command;
    }
}
