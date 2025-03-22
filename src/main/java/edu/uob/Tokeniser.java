package edu.uob;

import edu.uob.action.GameAction;
import edu.uob.entity.GameEntity;

import java.util.*;

public class Tokeniser {

    GameState state;
    List<String> tokens;

    public Tokeniser(GameState state, String command) {
        this.state = state;
        tokens = new LinkedList<>();
        Scanner scanner = new Scanner(command);
        scanner.useDelimiter(" ");

        while (scanner.hasNext()) {
            tokens.add(scanner.next());
        }
        scanner.close();
        System.out.println(tokens);
    }

    public EntityList getEntities() throws Exception {

        Set<GameEntity> entitySet = new HashSet<>();

        for (String entityName : tokens) {
            GameEntity entity = state.getEntityFromLocations(entityName);
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
            // TODO: Maybe remove from tokens list?

            if (!actions.add(action)) { // key phrase is used more than once
                throw new Exception();
            }
        }
        if (actions.isEmpty()) { // no action is specified
            throw new Exception();
        }
        if (actions.size() > 1) { // ambiguous command, multiple key phrases
            throw new Exception();
        }
        return actions.iterator().next();
    }
}
