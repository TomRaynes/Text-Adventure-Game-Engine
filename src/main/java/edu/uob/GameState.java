package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.action.*;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class GameState {

    private final Map<String, Set<GameAction>> actions;
    private final Map<String, Location> locations;
    private Location startLocation;
    private final GamePlayers players;

    public GameState(File actionsFile, File entitiesFile) throws Exception {
        locations = new HashMap<>();
        this.getEntitiesFromFile(entitiesFile);
        actions = this.getCustomActions(actionsFile);
        actions.putAll(this.getBasicActions());
        players = new GamePlayers(startLocation);
    }

    public String handleCommand(String input) {

        try {
            Player player = players.getPlayer(input.substring(0, input.indexOf(':')));
            int index = input.indexOf(':') + 1;
            String command = input.substring(index);
            CommandParser commandParser = new CommandParser(this, player, command);
            EntityList entities = commandParser.getEntities();
            GameAction action = commandParser.getAction(entities, player);
            return GameServer.joinStrings(action.performAction(player, entities), "\n");
        }
        catch (STAGException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return "ERROR\n";
        }
    }

    public Map<String, Location> getLocations() {
        return locations;
    }

    public Location getStartLocation() { // used in testing only
        return startLocation;
    }

    public static GameEntity getEntityFromLocations(String entityName,
                                                    Map<String, Location> locations) {
        GameEntity entity;

        for (Map.Entry<String, Location> location : locations.entrySet()) {
            entity = location.getValue().getEntity(entityName);

            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    public Set<GameAction> getAction(String keyPhrase) {
        return actions.get(keyPhrase);
    }

    public GamePlayers getPlayers() {
        return players;
    }

    public Map<String, Set<GameAction>> getActions() {
        return Map.copyOf(actions);
    }

    private Map<String, Set<GameAction>> getBasicActions() {
        Map<String, Set<GameAction>> actions = new HashMap<>();
        Set<GameAction> inventoryAction = Set.of(new InventoryAction());

        actions.put("inventory", inventoryAction);
        actions.put("inv", inventoryAction);
        actions.put("get", Set.of(new GetAction()));
        actions.put("drop", Set.of(new DropAction()));
        actions.put("goto", Set.of(new GotoAction()));
        actions.put("look", Set.of(new LookAction()));
        actions.put("health", Set.of(new HealthAction()));

        return actions;
    }

    private Map<String, Set<GameAction>> getCustomActions(File actionsFile) throws Exception {
        DocumentBuilder builder;
        Document document;

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(actionsFile);
        } catch (Exception | Error e) {
            throw new STAGException.ActionsFileParseException();
        }

        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getChildNodes();
        int index = 1;
        Element action;
        Map<String, Set<GameAction>> actions = new HashMap<>();

        while ((action = (Element) actionNodes.item(index)) != null) {

            if (!Objects.equals(action.getTagName(), "action")) {
                throw new STAGException.MalformedActionsFileException();
            }
            Element triggers = (Element) action.getElementsByTagName("triggers").item(0); // todo something
            Set<String> keyPhrases = this.getKeyPhrases(triggers);
            CustomAction customAction = new CustomAction(locations, action);

            for (String keyPhrase : keyPhrases) {
                String keyPhraseLowerCase = keyPhrase.toLowerCase();

                if (actions.containsKey(keyPhraseLowerCase)) {
                    actions.get(keyPhraseLowerCase).add(customAction);
                }
                else {
                    Set<GameAction> actionSet = new HashSet<>();
                    actionSet.add(customAction);
                    actions.put(keyPhraseLowerCase, actionSet);
                }
            }
            index += 2;
        }
        return actions;
    }

    private Set<String> getKeyPhrases(Element triggers) {
        int index = 0;
        Node node;
        Set<String> keyPhrases = new HashSet<>();

        while ((node = triggers.getElementsByTagName("keyphrase").item(index++)) != null) {
            keyPhrases.add(node.getTextContent());
        }

        return keyPhrases;
    }

    private void getEntitiesFromFile(File entitiesFile) throws Exception {
        Parser parser = new Parser();

        try (FileReader reader = new FileReader(entitiesFile)) {
            parser.parse(reader);
        }
        catch (Exception e) {
            throw new STAGException.EntitiesFileParseException();
        }

        Graph wholeDocument = parser.getGraphs().get(0);
        // get first sub-graph (start location)
        Graph startLocation = wholeDocument.getSubgraphs().get(0).getSubgraphs().remove(0);
        this.startLocation = this.addLocation(startLocation);

        // get remaining locations
        for (Graph graph : wholeDocument.getSubgraphs().get(0).getSubgraphs()) {
            this.addLocation(graph);
        }

        // add storeroom to locations if it doesn't exist
        if (!locations.containsKey("storeroom")) {
            locations.put("storeroom", new Location("storeroom", null));
        }

        // get paths
        for (Edge edge : wholeDocument.getSubgraphs().get(1).getEdges()) {
            Location fromLocation = locations.get(edge.getSource().getNode().getId().getId());
            Location toLocation = locations.get(edge.getTarget().getNode().getId().getId());
            fromLocation.addEntity(toLocation);
        }
    }

    private Location addLocation(Graph locationGraph) throws Exception {
        Location location = new Location(locationGraph);
        location.initialiseAllEntityLocations();

        locations.put(location.getName(), location);
        return location;
    }
}
