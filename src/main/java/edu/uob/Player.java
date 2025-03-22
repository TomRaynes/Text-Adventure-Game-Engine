package edu.uob;

import edu.uob.action.GameAction;
import edu.uob.entity.Artefact;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

import java.util.HashMap;
import java.util.Map;

public class Player {

    String name;
    Map<String, Artefact> inventory;
    Location location;
    int health;

    public Player(String name, Location startLocation) {
        this.name = name;
        inventory = new HashMap<>();
        location = startLocation;
        health = 3;
    }

    public boolean setLocation(Location location) throws Exception {

        if (this.location.getPaths().containsKey(location.getName())) {
            this.location = location;
            return true;
        }
        return false;
    }

    public Location getLocation() {
        return location;
    }

    public void addToInventory(Artefact artefact) {
        inventory.put(artefact.getName(), artefact);
    }

    public boolean removeFromInventory(Artefact artefact) {
        return inventory.remove(artefact.getName()) != null;
    }

    public GameEntity getEntityFromInventory(String entityName) {
        return inventory.get(entityName);
    }

    public String getName() {
        return name;
    }

    public Map<String, Artefact> getInventory() {
        return inventory;
    }
}
