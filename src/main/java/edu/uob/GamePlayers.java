package edu.uob;

import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

import java.util.HashMap;
import java.util.Map;

public class GamePlayers {

    Map<String, Player> players;

    public GamePlayers() {
        players = new HashMap<>();
    }

    public void addPlayer(String name, Location startLocation) {
        Player player = new Player(name, startLocation);
        players.put(name, player);
    }

    public Player getPlayer(String name) {

        if (players.containsKey(name)) {
            return players.get(name);
        }
        return null;
    }
}
