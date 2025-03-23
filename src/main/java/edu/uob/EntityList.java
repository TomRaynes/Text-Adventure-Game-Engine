package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.Furniture;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityList {

    Map<String, Set<GameEntity>> entities;

    public EntityList() {
        this.initialiseFields();
    }

    public EntityList(Set<GameEntity> entitySet) {
        this.initialiseFields();
        this.addEntities(entitySet);
    }

    private void initialiseFields() {
        entities = new HashMap<>();
        entities.put("locations", new HashSet<>());
        entities.put("artefacts", new HashSet<>());
        entities.put("furniture", new HashSet<>());
        entities.put("characters", new HashSet<>());
    }

    private void addEntityPrivate(GameEntity entity) {

        if (entity instanceof Location) {
            entities.get("locations").add(entity);
        }
        else if (entity instanceof Artefact) {
            entities.get("artefacts").add(entity);
        }
        else if (entity instanceof Furniture) {
            entities.get("furniture").add(entity);
        }
        else {
            entities.get("characters").add(entity);
        }
    }

    public void addEntity(GameEntity entity) {
        this.addEntityPrivate(entity);
    }

    public Set<GameEntity> getEntitiesOfType(String type) throws Exception {

        return switch (type) {
            case "location" -> entities.get("locations");
            case "artefact" -> entities.get("artefacts");
            case "furniture" -> entities.get("furniture");
            case "character" -> entities.get("characters");
            default -> throw new Exception();
        };
    }

    public boolean containsEntity(GameEntity entity) {

        if (entity instanceof Location) {
            return entities.get("locations").contains(entity);
        }
        else if (entity instanceof Artefact) {
            return entities.get("artefacts").contains(entity);
        }
        else if (entity instanceof Furniture) {
            return entities.get("furniture").contains(entity);
        }
        else {
            return entities.get("characters").contains(entity);
        }
    }

    public Set<GameEntity> toSet() throws Exception {

        Set<GameEntity> entities = new HashSet<>();
        entities.addAll(this.getEntitiesOfType("location"));
        entities.addAll(this.getEntitiesOfType("artefact"));
        entities.addAll(this.getEntitiesOfType("furniture"));
        entities.addAll(this.getEntitiesOfType("character"));
        return entities;
    }

    private void addEntities(Set<GameEntity> entitySet) {

        for (GameEntity entity : entitySet) {
            this.addEntityPrivate(entity);
        }
    }

    public boolean isEmpty() {

        for (Map.Entry<String, Set<GameEntity>> entry : entities.entrySet()) {

            if (!entry.getValue().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getSize() {

        int size = 0;

        for (Map.Entry<String, Set<GameEntity>> entry : entities.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, Set<GameEntity>> set : entities.entrySet()) {
            if (set.getValue().isEmpty()) continue;
            str.append(set.getKey()).append(":\n").append(entitySetToString(set.getValue())).append("\n");
        }
        return str.toString();
    }

    private String entitySetToString(Set<GameEntity> entities) {

        StringBuilder str = new StringBuilder();

        for (GameEntity entity : entities) {
            str.append(entity.getNameDescription()).append("\n");
        }
        return str.toString();
    }
}
