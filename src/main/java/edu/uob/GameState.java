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

    private Map<String, Set<GameAction>> actions;
    private Map<String, Location> locations;
    private Location startLocation;
    private GamePlayers players;

    public GameState(File actionsFile, File entitiesFile) throws Exception {
        locations = new HashMap<>();
        this.getEntitiesFromFile(entitiesFile);
        actions = this.getCustomActions(actionsFile);
        actions.putAll(this.getBasicActions());
        players = new GamePlayers(startLocation);
    }

    // ----- Methods for testing only ----- //
    public Map<String, Location> getLocations() {
        return locations;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    // ------------------------------------ //

    public String handleCommand(String input) {

        try {
            Player player = players.getPlayer(input.substring(0, input.indexOf(':')));
            int index = input.indexOf(':') + 1;
            String command = input.substring(index);
            Tokeniser tokeniser = new Tokeniser(this, locations, player, command);
            EntityList entities = tokeniser.getEntities();
            GameAction action = tokeniser.getAction(entities);
            return GameServer.joinStrings(action.performAction(player, entities), "\n");
        }
        catch (STAGException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return "ERROR\n";
        }

    }

    public static GameEntity getEntityFromLocations(String entityName,
                                                    Map<String, Location> locations) {
        GameEntity entity;

        for (Map.Entry<String, Location> location : locations.entrySet()) {
            entity = location.getValue().getEntity(entityName);

            if (entity != null) return entity;
        }
        return null;
    }

    public Set<GameAction> getAction(String keyPhrase) {

        if (actions.containsKey(keyPhrase)) {
            return actions.get(keyPhrase);
        }
        return null;
    }

    public GamePlayers getPlayers() {
        return players;
    }

    public Map<String, Set<GameAction>> getActions() {
        return actions;
    }

    private Map<String, Set<GameAction>> getBasicActions() {

        Map<String, Set<GameAction>> actions = new HashMap<>();
        Set<GameAction> inventoryAction = this.encapsulateActionInSet(new InventoryAction());
        actions.put("inventory", inventoryAction);
        actions.put("inv", inventoryAction);
        actions.put("get", this.encapsulateActionInSet(new GetAction()));
        actions.put("drop", this.encapsulateActionInSet(new DropAction()));
        actions.put("goto", this.encapsulateActionInSet(new GotoAction()));
        actions.put("look", this.encapsulateActionInSet(new LookAction()));
        actions.put("health", this.encapsulateActionInSet(new HealthAction()));
        return actions;
    }

    private Set<GameAction> encapsulateActionInSet(GameAction action) {
        Set<GameAction> actionSet = new HashSet<>();
        actionSet.add(action);
        return actionSet;
    }

    private Map<String, Set<GameAction>> getCustomActions(File actionsFile) throws Exception {

        DocumentBuilder builder;
        Document document;

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(actionsFile);
        }
        catch (Exception | Error e) {
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
            Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
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
            index+=2;
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
        FileReader reader;

        try {
            reader = new FileReader(entitiesFile);
            parser.parse(reader);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new STAGException.EntitiesFileParseException();
        }
        Graph wholeDocument = parser.getGraphs().get(0);
        // get first sub-graph (start location)
        Graph startLocation = wholeDocument.getSubgraphs().get(0).getSubgraphs().get(0);
        this.startLocation = new Location(startLocation);
        this.startLocation.initialiseAllEntityLocations();
        locations.put(this.startLocation.getName(), this.startLocation);
        wholeDocument.getSubgraphs().get(0).getSubgraphs().remove(0);

        // get remaining locations
        for (Graph graph : wholeDocument.getSubgraphs().get(0).getSubgraphs()) {
            Location location = new Location(graph);
            location.initialiseAllEntityLocations(); // set all entity locations to current location
            locations.put(location.getName(), location);
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

    public void printLocations() {

        for (Map.Entry<String, Location> location : locations.entrySet()) {
            System.out.println(location.getValue());
        }
    }

    public void printStartLocation() {
        System.out.println(GameServer.joinStrings("Start = ", startLocation.getName()));
    }

//    public void printActions() {
//
//        for (Map.Entry<String, GameAction> action : actions.entrySet()) {
//            System.out.println(action.getKey().toUpperCase());
//            System.out.println(action.getValue().toString());
//        }
//    }
}
