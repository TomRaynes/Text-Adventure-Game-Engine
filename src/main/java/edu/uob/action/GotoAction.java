package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

public class GotoAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        GameEntity entity = BasicAction.getSingularEntity(entities);
        player.moveLocation(entity);
        StringBuilder sb = new StringBuilder("You enter the ");
        sb.append(entity.getName());
        return sb.toString();
    }
}
