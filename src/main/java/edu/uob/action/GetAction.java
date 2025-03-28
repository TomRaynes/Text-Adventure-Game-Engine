package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;

public class GetAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        GameEntity entity = BasicAction.getSingularEntity(entities, "GET");
        Inventory inventory = player.getInventory();
        entity.moveEntity(inventory, player.getLocation());
        return GameServer.joinStrings(entity.getTitleCaseName(), " added to inventory");
    }
}
