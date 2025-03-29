package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.STAGException;
import edu.uob.entity.Location;

public class LookAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // look action has no subjects
            throw new STAGException.EntityInSubjectlessCommandException("LOOK");
        }
        Location location = player.getLocation();
        String description = GameServer.joinStrings("You are in ",
                                                     location.getDescription().toLowerCase(), ". ");
        if (location.isEmpty()) {
            return GameServer.joinStrings(description, "Nothing can be seen.");
        }
        return GameServer.joinStrings(description, "You can see:\n",
                                                    location.playersAndEntitiesToString(player));
    }
}
