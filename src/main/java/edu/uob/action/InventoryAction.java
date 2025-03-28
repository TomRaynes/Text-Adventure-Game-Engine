package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.STAGException;
import edu.uob.entity.Artefact;
import edu.uob.entity.Container;
import edu.uob.entity.Inventory;

import java.util.Map;

public class InventoryAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // inventory action has no subjects
            throw new STAGException.EntityInSubjectlessCommandException("INVENTORY");
        }
        Inventory inventory = player.getInventory();

        if (inventory.isEmpty()) {
            return "Your inventory is empty";
        }
        return GameServer.joinStrings("Your inventory contains:\n",
                                       Container.getEntitiesAsString(inventory.iterator()));

    }
}
