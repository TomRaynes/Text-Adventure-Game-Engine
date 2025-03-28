package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.GameState;
import edu.uob.Player;
import edu.uob.entity.Artefact;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;

public class DropAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        GameEntity entity = BasicAction.getSingularEntity(entities, "DROP");
        Inventory inventory = player.getInventory();
        entity.moveEntity(player.getLocation(), inventory);
        return GameServer.joinStrings(entity.getTitleCaseName(), " dropped from inventory");
    }
}
