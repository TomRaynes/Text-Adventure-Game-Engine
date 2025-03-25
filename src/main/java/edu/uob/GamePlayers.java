package edu.uob;

import edu.uob.entity.Artefact;
import edu.uob.entity.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GamePlayers implements Iterable<Player> {

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

    @Override
    public Iterator<Player> iterator() {
        return players.values().iterator();
    }

    public void forEach(Consumer<? super Player> action) {
        players.values().forEach(action);
    }
}
