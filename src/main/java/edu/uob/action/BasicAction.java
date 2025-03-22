package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.Player;
import edu.uob.entity.GameEntity;

import java.util.Set;

public abstract class BasicAction extends GameAction {

    public abstract String performAction(Player player, EntityList entities) throws Exception;

    protected static GameEntity getEntityOfType(String type, EntityList entities) throws Exception {

        if (entities.isEmpty()) { // no subject entity
            throw new Exception();
        }
        if (entities.getSize() > 1) { // must contain only 1 subject entity
            throw new Exception();
        }
        Set<GameEntity> entitySet = entities.getEntitiesOfType(type);

        if (entitySet.isEmpty()) { // subject entity wrong type
            throw new Exception();
        }
        return entitySet.iterator().next();
    }
}
