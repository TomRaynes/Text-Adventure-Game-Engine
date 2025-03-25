package edu.uob.entity;

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
}
