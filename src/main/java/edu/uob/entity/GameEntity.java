package edu.uob.entity;

import edu.uob.GameServer;
import edu.uob.STAGException;

import java.lang.Character;

public abstract class GameEntity implements Comparable<GameEntity> {

    private final String name;
    private final String description;
    private Container location;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void moveEntity(Container toLocation, Container fromLocation) throws Exception;

    protected Container getFromLocation(GameEntity entity, Container fromLocation,
                                                             Container toLocation) throws Exception {

        if (toLocation.containsEntity(entity) && toLocation instanceof Inventory) {
            throw new STAGException.EntityAlreadyInInventoryException(entity);
        }
        if (fromLocation == null) {
            fromLocation = location;
        }
        else if (fromLocation != location) {

            if (fromLocation instanceof Location) {
                throw new STAGException.EntityInDifferentLocationException(entity);
            }
            else throw new STAGException.EntityNotInInventoryException(entity);
        }
        return fromLocation;
    }

    public void setContainer(Container location) {
        this.location = location;
    }

    public Container getContainer() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getTitleCaseName() {
        StringBuilder sb = new StringBuilder(Character.toString(name.toUpperCase().charAt(0)));

        if (name.length() > 1) {
            sb.append(name.substring(1));
        }
        return sb.toString();
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(description);
        return sb.toString();
    }

    @Override
    public int compareTo(GameEntity entity) {
        return name.compareTo(entity.getName());
    }

    public String getNameDescription() {
        return GameServer.joinStrings(name, "; ", description);
    }
}
