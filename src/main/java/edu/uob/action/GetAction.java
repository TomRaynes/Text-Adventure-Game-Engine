package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Artefact;

public class GetAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        Artefact artefact = (Artefact) BasicAction.getEntityOfType("artefact", entities);

        if (!player.getLocation().removeArtefact(artefact)) {
            throw new Exception(); // artefact is in another location
        }
        player.addToInventory(artefact);
        StringBuilder sb = new StringBuilder(artefact.getTitleCaseName());
        sb.append(" added to inventory");
        return sb.toString();
    }
}
