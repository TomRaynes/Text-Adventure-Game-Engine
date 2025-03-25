package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;

public class GetAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        GameEntity entity = BasicAction.getSingularEntity(entities);
        Inventory inventory = player.getInventory();
        entity.moveEntity(inventory, player.getLocation());

        StringBuilder sb = new StringBuilder(entity.getTitleCaseName());
        sb.append(" added to inventory");
        return sb.toString();
    }
}
