package edu.uob.entity;

import java.util.Iterator;

public abstract class Container extends GameEntity {

    public Container(String name, String description) {
        super(name, description);
    }

    public abstract void addEntity(GameEntity entity) throws Exception;
    public abstract void removeEntity(GameEntity entity) throws Exception;
    public abstract GameEntity getEntity(String entityName);
    public abstract boolean containsEntity(GameEntity entity);
    public abstract boolean isEmpty();

    public static <T extends GameEntity> String getEntitiesAsString(Iterator<T> entities) {
        StringBuilder sb = new StringBuilder();

        while (entities.hasNext()) {
            sb.append(entities.next().getDescription()).append("\n");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 1);
    }
}
