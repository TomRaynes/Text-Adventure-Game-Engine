package edu.uob.entity;

public abstract class ObjectEntity extends GameEntity {

    private Container location;

    public ObjectEntity(String name, String description) {
        super(name, description);
    }

    public void moveEntity(Container toLocation, Container... fromLocations) throws Exception {

        Container fromLocation = null;

        for (Container container : fromLocations) {

            if (container == null) fromLocation = location;
            if (container == location) fromLocation = container;
        }
        if (fromLocation == null) {
            throw new Exception(); // Entity is in different location
        }

        fromLocation.removeEntity(this);
        toLocation.addEntity(this);
        location = toLocation;
    }

    public void setContainer(Container location) {
        this.location = location;
    }
}
