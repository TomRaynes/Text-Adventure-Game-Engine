package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Artefact;
import edu.uob.entity.Inventory;

public class DropAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        Artefact artefact = (Artefact) BasicAction.getEntityOfType("artefact", entities);
        Inventory inventory = player.getInventory();
        artefact.moveEntity(player.getLocation(), inventory);

        StringBuilder sb = new StringBuilder(artefact.getTitleCaseName());
        sb.append(" dropped from inventory");
        return sb.toString();
    }
}
