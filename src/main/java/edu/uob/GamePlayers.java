package edu.uob;

import edu.uob.entity.Location;

import java.util.*;
import java.util.function.Consumer;

public class GamePlayers implements Iterable<Player> {

    private Map<String, Player> players;
    private final Set<Character> legalChars;
    private final Location startLocation;

    public GamePlayers(Location startLocation) {
        this.startLocation = startLocation;
        players = new HashMap<>();
        legalChars = getLegalChars();
    }

    public Player getPlayer(String name) throws Exception {

        this.checkNameIsInvalid(name);

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

    private void checkNameIsInvalid(String name) throws Exception {

        Set<Character> illegalChars = new HashSet<>();

        for (int index = 0; index < name.length(); index++) {

            if (!legalChars.contains(name.charAt(index))) {
                illegalChars.add(name.charAt(index));
            }
        }
        if (!illegalChars.isEmpty()) {
            String illegalCharString = this.getIllegalCharString(illegalChars);

            throw new STAGException.IllegalPlayerNameException(illegalCharString,
                                                               illegalChars.size() > 1);
        }
    }

    private String getIllegalCharString(Set<Character> illegalChars) {
        StringBuilder sb = new StringBuilder();

        for (char ch : illegalChars) {

            if (ch == ',') {
                // replacing all ',' with 'C'
                sb.append("'").append('C').append("'").append(", ");
            }
            else sb.append("'").append(ch).append("'").append(", ");
        }
        String str = sb.toString();
        // remove comma at the end
        str = str.substring(0, str.length() - 2);

        if (illegalChars.size() > 1) {
            // replace last occurrence of ', ' with ' and '
            int lastCommaIndex = str.lastIndexOf(',');
            str = GameServer.joinStrings(str.substring(0, lastCommaIndex), " and",
                                         str.substring(lastCommaIndex + 1));
        }
        // replace all 'C' with ','
        str = str.replace('C', ',');
        return str;
    }

    private Set<Character> getLegalChars() {
        Set<Character> legalChars = new HashSet<>();

        for (char ch = 'a'; ch <= 'z'; ch++) {
            legalChars.add(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            legalChars.add(ch);
        }
        legalChars.add(' ');
        legalChars.add('\'');
        legalChars.add('-');
        return legalChars;
    }

    @Override
    public Iterator<Player> iterator() {
        return players.values().iterator();
    }

    public void forEach(Consumer<? super Player> action) {
        players.values().forEach(action);
    }
}
