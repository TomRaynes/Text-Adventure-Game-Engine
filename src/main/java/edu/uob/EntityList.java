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

    public void addEntity(Location location) {
        entities.get("locations").add(location);
    }

    public void addEntity(Artefact artefact) {
        entities.get("artefacts").add(artefact);
    }

    public void addEntity(Furniture furniture) {
        entities.get("furniture").add(furniture);
    }

    public void addEntity(Character character) {
        entities.get("characters").add(character);
    }
}
