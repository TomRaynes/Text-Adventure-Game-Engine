package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameState;
import edu.uob.Player;
import edu.uob.entity.Artefact;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;

public class DropAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        GameEntity entity = BasicAction.getSingularEntity(entities);
        Inventory inventory = player.getInventory();
        entity.moveEntity(player.getLocation(), inventory);

        StringBuilder sb = new StringBuilder(entity.getTitleCaseName());
        sb.append(" dropped from inventory");
        return sb.toString();
    }
}
