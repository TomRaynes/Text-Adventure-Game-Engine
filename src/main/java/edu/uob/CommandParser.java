package edu.uob;

import edu.uob.action.BasicAction;
import edu.uob.action.CustomAction;
import edu.uob.action.GameAction;
import edu.uob.entity.GameEntity;

import java.util.*;

public class CommandParser {

    private final GameState state;
    private final Player player;
    private final List<String> tokens;

    public CommandParser(GameState state, Player player, String command) {
        this.state = state;
        this.player = player;
        this.tokens = new LinkedList<>();
        Scanner scanner = new Scanner(this.normaliseCommand(command));
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

            entitySet.add(entity);
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

    public GameAction getAction(EntityList commandEntities, Player player) throws Exception {
        Set<GameAction> actions = this.getMatchedActions();

        if (actions.isEmpty()) { // no action is specified
            throw new STAGException.NoActionFoundException();
        }
        if (actions.size() > 1) {
            actions = this.filterByEntities(actions, player); // match with available entities
            actions = this.filterByEntities(actions, commandEntities); // match with specified entities

            if (actions.size() != 1) { // ambiguous command, multiple key phrases
                throw new STAGException.AmbiguousCommandException();
            }
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

    private Set<GameAction> getMatchedActions() {
        // holds all actions that use any single-word key phrase present in command
        Set<GameAction> matchedActions = new HashSet<>();

        for (String keyPhrase : tokens) {
            // hold all actions that use trigger containing specific key phrase
            Set<GameAction> actionSet = this.getPriorityAction(keyPhrase, tokens.indexOf(keyPhrase));

            if (actionSet == null) {
                continue; // token is not key phrase or component word of key phrase
            }
            matchedActions.addAll(actionSet);
        }
        return matchedActions;
    }

    private Set<GameAction> getPriorityAction(String trigger, int index) {
        Set<GameAction> triggerMatches = state.getAction(trigger);

        if (++index == tokens.size()) {
            return triggerMatches;
        }
        String nextTrigger = GameServer.joinStrings(trigger, " ", tokens.get(index));
        Set<GameAction> nextTriggerMatches = this.getPriorityAction(nextTrigger, index);

        if (nextTriggerMatches == null) {
            return triggerMatches;
        }
        return nextTriggerMatches;
    }

    private Set<GameAction> filterByEntities(Set<GameAction> actions, Player player)
                                                                        throws Exception {
        Set<GameAction> matchedActions = new HashSet<>();

        for (GameAction action : actions) {
            if (action instanceof CustomAction customAction) {
                if (customAction.isPerformable(player)) {
                    matchedActions.add(action);
                }
            }
            else throw new STAGException.AmbiguousCommandException();
        }
        return matchedActions;
    }

    private Set<GameAction> filterByEntities(Set<GameAction> actions, EntityList entities)
                                                                            throws Exception {
        Set<GameAction> matchedActions = new HashSet<>();
        int currentMatches = 0;

        for (GameAction action : actions) {
            if (action instanceof CustomAction customAction) {
                EntityList subjects = customAction.getSubjects();
                int matches = 0;

                for (GameEntity entity : entities) {
                    if (subjects.containsEntity(entity)) {
                        matches++;
                    }
                }

                if (matches > currentMatches) {
                    matchedActions = new HashSet<>();
                    matchedActions.add(action);
                    currentMatches = matches;
                }
                else if (matches == currentMatches) {
                    matchedActions.add(action);
                }
            }
            else throw new STAGException.AmbiguousCommandException();
        }
        return matchedActions;
    }

    private String normaliseCommand(String command) {

        while (command.contains("  ")) {
            command = command.replace("  ", " ");
        }
        return command.replaceAll("[.,:;!?]", "").toLowerCase();
    }
}
