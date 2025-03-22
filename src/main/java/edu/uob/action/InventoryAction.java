package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Artefact;

import java.util.Map;

public class InventoryAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // inventory action has no subjects
            throw new Exception();
        }
        Map<String, Artefact> inventory = player.getInventory();

        if (inventory.isEmpty()) {
            return "Your inventory is empty";
        }
        StringBuilder sb = new StringBuilder("Your inventory contains:\n");

        for (Map.Entry<String, Artefact> entry : inventory.entrySet()) {
            sb.append(entry.getValue().getDescription()).append("\n");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 1);
    }
}
