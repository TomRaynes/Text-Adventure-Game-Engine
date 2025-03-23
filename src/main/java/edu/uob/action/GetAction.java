package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Artefact;
import edu.uob.entity.Inventory;

public class GetAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        Artefact artefact = (Artefact) BasicAction.getEntityOfType("artefact", entities);
        Inventory inventory = player.getInventory();
        artefact.moveEntity(inventory, player.getLocation());

        StringBuilder sb = new StringBuilder(artefact.getTitleCaseName());
        sb.append(" added to inventory");
        return sb.toString();
    }
}
