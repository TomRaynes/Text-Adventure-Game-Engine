package edu.uob.entity;

import java.lang.Character;

public abstract class GameEntity {

    private final String name;
    private final String description;
    private Container location;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void moveEntity(Container toLocation, Container... fromLocations) throws Exception;

    protected Container getFromLocation(Container... fromLocations) throws Exception {

        Container fromLocation = null;

        for (Container container : fromLocations) {

            if (container == null) {
                fromLocation = location;
                break;
            }
            if (container == location) fromLocation = container;
        }
        if (fromLocation == null) {
            throw new Exception(); // Entity is in different location
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
        // TODO: remove string concatenation
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(description);
        return sb.toString();
    }

    public String getNameDescription() {
        return name + ": " + description;
    }
}
