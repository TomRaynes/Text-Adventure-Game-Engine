package edu.uob;

import edu.uob.entity.GameEntity;

import java.util.*;
import java.util.function.Consumer;

public class EntityList implements Iterable<GameEntity> {

    Set<GameEntity> entities;

    public EntityList() {
        entities = new HashSet<>();
    }

    public EntityList(Set<GameEntity> entitySet) {
        entities = new HashSet<>(entitySet);
    }

    public EntityList(EntityList... entityLists) {
        entities = new HashSet<>();

        for (EntityList entityList : entityLists) {
            this.addEntities(entityList);
        }
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public boolean containsEntity(GameEntity entity) {
        return entities.contains(entity);
    }

    private void addEntities(EntityList entities) {

        for (GameEntity entity : entities) {
            this.addEntity(entity);
        }
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public int getSize() {
        return entities.size();
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        for (GameEntity entity : entities) {
            str.append(entity.getNameDescription()).append("\n");
        }
        return str.toString();
    }

    @Override
    public Iterator<GameEntity> iterator() {
        return entities.iterator();
    }

    @Override
    public void forEach(Consumer<? super GameEntity> action) {
        entities.forEach(action);
    }
}
