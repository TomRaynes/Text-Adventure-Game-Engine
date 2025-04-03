package edu.uob;

import edu.uob.action.GameAction;

public class KeyActionPair {

    private final GameAction action;
    private final String trigger;

    public KeyActionPair(GameAction action, String trigger) {
        this.action = action;
        this.trigger = trigger;
    }

    public GameAction getAction() {
        return action;
    }

    public String getKey() {
        return trigger;
    }
}
