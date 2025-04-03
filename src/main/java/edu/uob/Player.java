package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Inventory;
import edu.uob.entity.Location;

public class Player implements Comparable<Player> {

    private final String name;
    private final Inventory inventory;
    private Location location;
    private final Location startLocation;
    private int health;

    public Player(String name, Location startLocation) {
        this.name = name;
        inventory = new Inventory(name, null);
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
            return this.deathSequence();
        }
        return "";
    }

    public String getHealth() {
        return Integer.toString(health);
    }

    private String deathSequence() throws Exception {

        for (Artefact artefact : inventory) {
            artefact.moveEntity(location, inventory);
        }
        location = startLocation;

        return "You died and lost all of your items. You must return to the start of the game";
    }

    public void moveLocation(GameEntity entity) throws Exception {

        if (entity instanceof Location toLocation) {

            Location fromLocation = location;

            if (fromLocation == toLocation) {
                throw new STAGException.PlayerAlreadyInLocationException(location);
            }
            if (fromLocation.getPaths().containsKey(toLocation.getName())) {
                location = toLocation;
            }
            // no path to location
            else throw new STAGException.NoPathToLocationException(fromLocation, toLocation);

            fromLocation.removePlayer(this);
            location.addPlayer(this);
        }
        // entity is not a location
        else throw new STAGException.EntityNotLocationException(entity);
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

    @Override
    public int compareTo(Player player) {
        return name.compareTo(player.getName());
    }
}
