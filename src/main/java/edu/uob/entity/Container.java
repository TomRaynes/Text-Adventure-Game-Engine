package edu.uob.entity;

public abstract class Container extends GameEntity {

    public Container(String name, String description) {
        super(name, description);
    }

    public abstract void addEntity(GameEntity entity);
    public abstract void removeEntity(GameEntity entity);
    public abstract GameEntity getEntity(String entityName);
    public abstract boolean contains(GameEntity entity);
}
