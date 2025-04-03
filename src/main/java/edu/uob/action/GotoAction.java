package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.entity.GameEntity;

public class GotoAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {
        GameEntity entity = BasicAction.getSingularEntity(entities,"GOTO");
        player.moveLocation(entity);
        return GameServer.joinStrings("You enter the ", entity.getName());
    }
}
