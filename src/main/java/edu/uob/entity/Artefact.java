package edu.uob.entity;

public class Artefact extends GameEntity {

    public Artefact(String name, String description) {
        super(name, description);
    }

    public void moveEntity(Container toLocation, Container fromLocation) throws Exception {
        fromLocation = this.getFromLocation(this, fromLocation, toLocation);
        toLocation.addEntity(this);
        fromLocation.removeEntity(this);
        this.setContainer(toLocation);
    }
}
