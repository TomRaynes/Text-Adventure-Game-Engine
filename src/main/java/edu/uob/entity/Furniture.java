package edu.uob.entity;

public class Furniture extends GameEntity {

    public Furniture(String name, String description) {
        super(name, description);
    }

    public void moveEntity(Container toLocation, Container fromLocation) throws Exception {

        fromLocation = this.getFromLocation(this, fromLocation, toLocation);
        toLocation.addEntity(this);
        fromLocation.removeEntity(this);
        this.setContainer(toLocation);
    }
}
