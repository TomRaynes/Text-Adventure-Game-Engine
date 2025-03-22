package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Location;

public class GotoAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        Location location = (Location) BasicAction.getEntityOfType("location", entities);

        if (!player.setLocation(location)) {
            throw new Exception(); // no paths from current location to location
        }
        StringBuilder sb = new StringBuilder("You enter the ");
        sb.append(location.getName());
        return sb.toString();
    }
}
