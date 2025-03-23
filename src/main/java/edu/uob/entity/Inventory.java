package edu.uob.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Inventory extends Container implements Iterable<Map.Entry<String, Artefact>> {

    Map<String, Artefact> artefacts;

    public Inventory(String name, String description) {
        super(name, description);
        artefacts = new HashMap<>();
    }

    public void addEntity(GameEntity entity) {

        if (entity instanceof Artefact artefact) {
            artefacts.put(artefact.getName(), artefact);
        }
    }

    public void removeEntity(GameEntity entity) {
        artefacts.remove(entity.getName());
    }

    public GameEntity getEntity(String entityName) {
        return artefacts.get(entityName);
    }

    public boolean contains(GameEntity entity) {
        return artefacts.containsKey(entity.getName());
    }

    public boolean isEmpty() {
        return artefacts.isEmpty();
    }

    @Override
    public Iterator<Map.Entry<String, Artefact>> iterator() {
        return artefacts.entrySet().iterator();
    }
}
