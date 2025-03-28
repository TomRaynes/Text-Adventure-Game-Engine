package edu.uob.entity;

import java.util.Iterator;

public abstract class Container extends GameEntity {

    public Container(String name, String description) {
        super(name, description);
    }

    public abstract void addEntity(Artefact artefact);
    public abstract void addEntity(Character character) throws Exception;
    public abstract void addEntity(Furniture furniture) throws Exception;
    public abstract void addEntity(Location path) throws Exception;
    public abstract void removeEntity(Artefact artefact);
    public abstract void removeEntity(Character character) throws Exception;
    public abstract void removeEntity(Furniture furniture) throws Exception;
    public abstract void removeEntity(Location path) throws Exception;
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
