package edu.uob.action;

import edu.uob.Player;

public abstract class BasicAction extends GameAction {

    public abstract void performAction(Player player, String command);
}
