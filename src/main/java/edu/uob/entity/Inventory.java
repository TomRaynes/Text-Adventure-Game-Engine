package edu.uob.entity;

import edu.uob.GameServer;
import edu.uob.STAGException;

import java.util.*;
import java.util.function.Consumer;

public class Inventory extends Container implements Iterable<Artefact> {

    private Map<String, Artefact> artefacts;

    public Inventory(String name, String description) {
        super(name, description);
        artefacts = new HashMap<>();
    }
    public Set<Artefact> getArtefactsSet() {
        return new HashSet<>(artefacts.values());
    }

    public void moveEntity(Container toLocation, Container fromLocation) throws Exception {
        throw new STAGException.TryingToMoveContainerException(this);
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
        Set<Artefact> orderedInventory = new TreeSet<>(artefacts.values());
        return orderedInventory.iterator();
    }

    @Override
    public void forEach(Consumer<? super Artefact> action) {
        artefacts.values().forEach(action);
    }

    public void addEntity(Character character) throws Exception {
        throw new STAGException.IllegalMoveToInventoryException(character);
    }

    public void addEntity(Furniture furniture) throws Exception {
        throw new STAGException.IllegalMoveToInventoryException(furniture);
    }

    public void addEntity(Location path) throws Exception {
        throw new STAGException.IllegalMoveToInventoryException(path);
    }

    public void removeEntity(Character character) throws Exception {
        throw new STAGException.EntityNotInInventoryException(character);
    }

    public void removeEntity(Furniture furniture) throws Exception {
        throw new STAGException.EntityNotInInventoryException(furniture);
    }

    public void removeEntity(Location path) throws Exception {
        throw new STAGException.EntityNotInInventoryException(path);
    }

    @Override
    public String toString() {

        if (this.isEmpty()) {
            return "";
        }

        String artefactList = "";

        for (Artefact artefact : artefacts.values()) {
            artefactList = GameServer.joinStrings(artefactList, artefact.getDescription(), "\n");
        }
        return artefactList.substring(0, artefactList.length() - 1);
    }
}
