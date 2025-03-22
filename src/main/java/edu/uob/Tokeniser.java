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
            if (entity == null) continue; // token is not an entity

            if (!entitySet.add(entity)) { // entity is given more than once
                throw new Exception();
            }
        }
        return new EntityList(entitySet);
    }

    public GameAction getAction() throws Exception {

        Set<GameAction> actions = new HashSet<>();

        for (String keyPhrase : tokens) {
            GameAction action = state.getAction(keyPhrase);
            if (action == null) continue; // token is not key phrase

            // TODO: if action set size > 1, determine which is the relevant action from the subjects

            if (!actions.add(action)) { // key phrase is used more than once
                throw new Exception();
            }
        }
        if (actions.isEmpty()) { // keyPhrase may contain multiple words
            GameAction action = this.getMultiWordAction();
            if (action != null) actions.add(action);
        }
        if (actions.isEmpty()) { // no action is specified
            throw new Exception();
        }
        if (actions.size() > 1) { // ambiguous command, multiple key phrases
            throw new Exception();
        }
        return actions.iterator().next();
    }

    public GameAction getActionFromEntities(Set<GameAction> actions, Set<GameEntity> entities) {

        GameAction intendedAction = null;
        int currentMatches = 0;

        for (GameAction action : actions) { // assume actions with same trigger phrase are CustomActions
            EntityList subjects = ((CustomAction) action).getSubjects();
            int matches = 0;

            for (GameEntity entity : entities) {
                if (subjects.containsEntity(entity)) matches++;
            }
            if (matches > currentMatches) {
                intendedAction = action;
                currentMatches = matches;
            }
        }
        return intendedAction;
    }

    private GameAction getMultiWordAction() {

        for (Map.Entry<String, GameAction> entry : state.getActions().entrySet()) {

            if (command.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
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
