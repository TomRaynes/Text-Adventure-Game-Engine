package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.STAGException;
import edu.uob.entity.GameEntity;

public abstract class BasicAction extends GameAction {

    protected static GameEntity getSingularEntity(EntityList entities, String command) throws Exception {

        if (entities.isEmpty()) { // no subject entity
            throw new STAGException.NoSpecifiedEntityException(command);
        }
        if (entities.getSize() > 1) { // must contain only 1 subject entity
            throw new STAGException.MultipleEntitiesInBasicCommandException(command);
        }
        return entities.iterator().next();
    }
}
