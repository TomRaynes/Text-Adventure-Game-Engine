package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;

public class HealthAction extends BasicAction {

    public String performAction(Player player, EntityList entities) throws Exception {

        if (!entities.isEmpty()) { // health action has no subjects
            throw new Exception();
        }
        StringBuilder sb = new StringBuilder("Your health level is ");
        sb.append(player.getHealth());
        return sb.toString();
    }
}
