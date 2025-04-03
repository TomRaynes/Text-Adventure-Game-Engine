package edu.uob;

import edu.uob.action.BasicAction;
import edu.uob.action.CustomAction;
import edu.uob.action.GameAction;
import edu.uob.entity.GameEntity;

import java.util.*;

public class CommandParser {

    private final String command;
    private final GameState state;
    private final Player player;
    private final List<String> tokens;

    public CommandParser(GameState state, Player player, String command) {
        this.command = this.normaliseCommand(command);
        this.state = state;
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
            GameEntity entity = GameState.getEntityFromLocations(entityName, state.getLocations());

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

        Set<GameAction> actions = this.getMatchedActions(entities);

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
        GameAction action = actions.iterator().next();

        if (action instanceof BasicAction) {
            this.checkActionOrdering();
        }
        return action;
    }

    private void checkActionOrdering() throws Exception {

        String firstToken = tokens.get(0).toLowerCase();

        for (String trigger : this.getBasicCommandTriggers()) {
            if (firstToken.equals(trigger)) return;
        }
        throw new STAGException.IncorrectTokenOrderException();
    }

    private Set<String> getBasicCommandTriggers() {
        return Set.of("look", "inv", "inventory", "get", "drop", "goto", "health");
    }

    private Set<GameAction> getMatchedActions(EntityList entities) throws Exception {
        // holds all actions that use any single-word key phrase present in command
        Set<GameAction> matchedActions = new HashSet<>();

        for (String keyPhrase : tokens) {
            // hold all actions that use specific key phrase
            Set<GameAction> actionSet = this.getPriorityAction(keyPhrase, tokens.indexOf(keyPhrase));
            if (actionSet == null) continue; // token is not key phrase
            GameAction action;

            // if action set size > 1, determine which is the relevant action from the subjects
            if (actionSet.size() > 1) { //TODO: do this after get priority actions
                action = this.getActionFromEntities(actionSet, entities);

                if (action == null) continue; // cant match an action with entities
            }
            else action = actionSet.iterator().next();

            if (!matchedActions.add(action)) { // key phrase is used more than once
                throw new STAGException.DuplicateKeyPhraseInCommandException();
            }
        }
        return matchedActions;
    }

    private Set<GameAction> getPriorityAction(String trigger, int index) {
        Set<GameAction> triggerMatches = state.getAction(trigger);

        if (index + 1 == tokens.size()) {
            return triggerMatches;
        }
        String nextTrigger = GameServer.joinStrings(trigger, " ", tokens.get(index + 1));
        Set<GameAction> nextTriggerMatches = this.getPriorityAction(nextTrigger, index+1);

        if (nextTriggerMatches == null) {
            return triggerMatches;
        }
        return nextTriggerMatches;
    }

    public GameAction getActionFromEntities(Set<GameAction> actions, EntityList entities)
                                                                            throws Exception {
        Set<GameAction> matchedActions = null;
        int currentMatches = 0;

        for (GameAction action : actions) {
            EntityList subjects = ((CustomAction) action).getSubjects();
            int matches = 0;

            for (GameEntity entity : entities) {
                if (subjects.containsEntity(entity)) matches++;
            }
            if (matches > currentMatches) {
                matchedActions = new HashSet<>();
                matchedActions.add(action);
                currentMatches = matches;
            }
            else if (matchedActions != null && matches == currentMatches) {
                matchedActions.add(action);
            }
        }
        if (matchedActions == null) {
            return null;
        }
        if (matchedActions.size() > 1) {
            throw new STAGException.AmbiguousCommandException();
        }
        return matchedActions.iterator().next();
    }

    private GameAction getMultiWordAction(EntityList entities) throws Exception {

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

        while (command.contains("  ")) {
            command = command.replace("  ", " ");
        }
        return command.replaceAll("[.,:;!?]", "").toLowerCase();
    }
}
