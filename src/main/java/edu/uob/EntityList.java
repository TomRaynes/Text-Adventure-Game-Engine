package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.Character;
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
        entities = new HashMap<>();
        entities.put("locations", new HashSet<>());
        entities.put("artefacts", new HashSet<>());
        entities.put("furniture", new HashSet<>());
        entities.put("characters", new HashSet<>());
    }

    public void addEntity(GameEntity entity) {

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

    public String toString() {

        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, Set<GameEntity>> set : entities.entrySet()) {
            if (set.getValue().isEmpty()) continue;
            str.append(set.getKey()).append(":\n").append(entitySetToString(set.getValue())).append("\n");
        }
        return str.toString();
    }

    private String entitySetToString(Set<GameEntity> entities) {
        //if (entities.isEmpty()) return "";

        StringBuilder str = new StringBuilder();

        for (GameEntity entity : entities) {
            str.append(entity.getNameDescription()).append("\n");
        }
        return str.toString();
    }
}
