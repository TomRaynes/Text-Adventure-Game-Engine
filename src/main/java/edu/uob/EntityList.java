package edu.uob;

import edu.uob.entity.Container;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;

import java.util.*;
import java.util.function.Consumer;

public class EntityList implements Iterable<GameEntity> {

    private Set<GameEntity> entities;

    public EntityList() {
        entities = new TreeSet<>();
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

    public boolean containsEntityInForeignInventory(Container inventory) {

        for (GameEntity entity : entities) {
            Container container = entity.getContainer();

            if (container instanceof Inventory && !Objects.equals(inventory, container)) {
                return true;
            }
        }
        return false;
    }

    public void addEntity(GameEntity entity) {
        entities.add(entity);
    }

    public void removeEntity(GameEntity entity) {
        entities.remove(entity);
    }

    public boolean containsEntity(GameEntity entity) {
        return entities.contains(entity);
    }

    private void addEntities(EntityList entities) {

        for (GameEntity entity : entities) {
            this.entities.add(entity);
        }
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public int getSize() {
        return entities.size();
    }

    @Override
    public String toString() {

        if (this.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (GameEntity entity : entities) {
            sb.append(entity.getName()).append(", ");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 2);
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
