package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;
import edu.uob.entity.Location;

import java.util.Map;

public class Player {

    private final String name;
    private Inventory inventory;
    private Location location;
    private final Location startLocation;
    private int health;

    public Player(String name, Location startLocation) {
        this.name = name;
        inventory = new Inventory(null, null);
        location = startLocation;
        this.startLocation = startLocation;
        health = 3;
    }

    public void addPlayerToLocation() {
        location.addPlayer(this);
    }

    public String updateHealth(int healthEffect) throws Exception {
        health += healthEffect;

        if (health > 3) {
            health = 3;
        }
        if (health < 1) {
            health = 3;
            return this.runDeathSequence();
        }
        return "";
    }

    public int getHealth() {
        return health;
    }

    private String runDeathSequence() throws Exception {

        for (Map.Entry<String, Artefact> entry : inventory) {
            entry.getValue().moveEntity(location, inventory);
        }
        location = startLocation;

        return "You died and lost all of your items. You must return to the start of the game";
    }

    public void moveLocation(Location toLocation) throws Exception {

        Location fromLocation = this.location;

        if (fromLocation.getPaths().containsKey(toLocation.getName())) {
            this.location = toLocation;
        }
        else throw new Exception(); // no path to location

        fromLocation.removePlayer(this);
        this.location.addPlayer(this);
    }

    public Location getLocation() {
        return location;
    }

    public GameEntity getEntityFromInventory(String entityName) {
        return inventory.getEntity(entityName);
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
