package edu.uob;

import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;
import edu.uob.entity.Location;

public class Player {

    String name;
    Inventory inventory;
    Location location;
    int health;

    public Player(String name, Location startLocation) {
        this.name = name;
        inventory = new Inventory(null, null);
        location = startLocation;
        health = 3;
    }

    public void addPlayerToLocation() {
        location.addPlayer(this);
    }

    public void updateHealth(int healthEffect) {
        health += healthEffect;
    }

    public boolean setLocation(Location location) throws Exception {

        if (this.location.getPaths().containsKey(location.getName())) {
            this.location = location;
            return true;
        }
        return false;
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
