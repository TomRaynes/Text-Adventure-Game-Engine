package edu.uob;

import edu.uob.entity.Artefact;
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
}
