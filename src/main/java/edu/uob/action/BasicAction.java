package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.entity.GameEntity;

public abstract class BasicAction extends GameAction {

    protected static GameEntity getSingularEntity(EntityList entities) throws Exception {

        if (entities.isEmpty()) { // no subject entity
            throw new Exception();
        }
        if (entities.getSize() > 1) { // must contain only 1 subject entity
            throw new Exception();
        }
        return entities.iterator().next();
    }
}
