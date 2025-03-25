package edu.uob.entity;

public class Artefact extends ObjectEntity {

    public Artefact(String name, String description) {
        super(name, description);
    }

    @Override
    public void moveEntity(Container toLocation, Container... fromLocations) throws Exception {

        Container fromLocation = this.getFromLocation(fromLocations);
        toLocation.addEntity(this);
        fromLocation.removeEntity(this);
        this.setContainer(toLocation);
    }
}
