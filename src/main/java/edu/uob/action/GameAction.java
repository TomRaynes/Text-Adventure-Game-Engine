package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;

public abstract class GameAction {

    public abstract String performAction(Player player, EntityList entities) throws Exception;
}
