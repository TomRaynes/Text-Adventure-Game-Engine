package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Artefact;

public class DropAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        Artefact artefact = (Artefact) BasicAction.getEntityOfType("artefact", entities);

        if (!player.removeFromInventory(artefact)) { // artefact not in inventory
            throw new Exception();
        }
        player.getLocation().addArtefact(artefact);
        StringBuilder sb = new StringBuilder(artefact.getTitleCaseName());
        sb.append(" dropped from inventory");
        return sb.toString();
    }
}
