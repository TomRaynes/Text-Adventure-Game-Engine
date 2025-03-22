package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.Location;

public class LookAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // inventory action has no subjects
            throw new Exception();
        }
        Location location = player.getLocation();
        StringBuilder sb = new StringBuilder("You are in ");
        sb.append(location.getDescription().toLowerCase()).append("\n");

        if (location.hasNoEntities()) {
            sb.append("Nothing can be seen");
            return sb.toString();
        }
        sb.append("You can see:\n").append(location.EntitiesToString());
        return sb.toString();
    }
}
