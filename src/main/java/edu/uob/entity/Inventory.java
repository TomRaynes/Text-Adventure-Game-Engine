package edu.uob.entity;

import edu.uob.STAGException;

import java.util.*;
import java.util.function.Consumer;

public class Inventory extends Container implements Iterable<Artefact> {

    private final Map<String, Artefact> artefacts;

    public Inventory(String name, String description) {
        super(name, description);
        artefacts = new HashMap<>();
    }

    public void moveEntity(Container toLocation, Container fromLocation) throws Exception {
        throw new STAGException.TryingToMoveContainerException(this);
    }

    public void addEntity(GameEntity entity) throws Exception {
        if (entity instanceof Artefact artefact) {
            artefacts.put(artefact.getName(), artefact);
        } else {
            throw new STAGException.IllegalMoveToInventoryException(entity);
        }
    }

    public void removeEntity(GameEntity entity) throws Exception {
        if (entity instanceof Artefact) {
            artefacts.remove(entity.getName());
        }
        else {
            throw new STAGException.EntityNotInInventoryException(entity);
        }
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
        Set<Artefact> orderedInventory = new TreeSet<>(artefacts.values());
        return orderedInventory.iterator();
    }

    @Override
    public void forEach(Consumer<? super Artefact> action) {
        artefacts.values().forEach(action);
    }
}
