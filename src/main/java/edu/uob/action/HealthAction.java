package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.STAGException;

public class HealthAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // health action has no subjects
            throw new STAGException.EntityInSubjectlessCommandException("HEALTH");
        }
        return GameServer.joinStrings("Your health level is ", player.getHealth());
    }
}
