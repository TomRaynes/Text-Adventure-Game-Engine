package edu.uob;

import edu.uob.entity.Location;

import java.util.HashMap;
import java.util.Map;

public class GamePlayers {

    Map<String, Player> players;
    Location startLocation;

    public GamePlayers(Location startLocation) {
        this.startLocation = startLocation;
        players = new HashMap<>();
    }

    public Player getPlayer(String name) {

        if (!players.containsKey(name)) {
            this.addPlayer(name);
        }
        return players.get(name);
    }

    private void addPlayer(String name) {
        Player player = new Player(name, startLocation);
        player.addPlayerToLocation();
        players.put(name, player);
    }
}
