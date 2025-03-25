package edu.uob.entity;

import edu.uob.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class Inventory extends Container implements Iterable<Artefact> {

    Map<String, Artefact> artefacts;

    public Inventory(String name, String description) {
        super(name, description);
        artefacts = new HashMap<>();
    }

    public void moveEntity(Container toLocation, Container... fromLocations) throws Exception {
        throw new Exception(); // cant move inventory
    }

    public void addEntity(Artefact artefact) {
        artefacts.put(artefact.getName(), artefact);
    }

    public void removeEntity(Artefact artefact) {
        artefacts.remove(artefact.getName());
    }

    public GameEntity getEntity(String entityName) {
        return artefacts.get(entityName);
    }

    public boolean containsEntity(GameEntity entity) {
        return artefacts.containsKey(entity.getName());
    }

    public boolean isEmpty() {
        return artefacts.isEmpty();
    }

    @Override
    public Iterator<Artefact> iterator() {
        return artefacts.values().iterator();
    }

    public void forEach(Consumer<? super Artefact> action) {
        artefacts.values().forEach(action);
    }

    public void addEntity(Character character) throws Exception {
        throw new Exception(); // only artefacts can be added
    }

    public void addEntity(Furniture furniture) throws Exception {
        throw new Exception(); // only artefacts can be added
    }

    public void addEntity(Location path) throws Exception {
        throw new Exception(); // only artefacts can be added
    }

    public void removeEntity(Character character) throws Exception {
        throw new Exception(); // only artefacts can be removed
    }

    public void removeEntity(Furniture furniture) throws Exception {
        throw new Exception(); // only artefacts can be removed
    }

    public void removeEntity(Location path) throws Exception {
        throw new Exception(); // only artefacts can be removed
    }
}
