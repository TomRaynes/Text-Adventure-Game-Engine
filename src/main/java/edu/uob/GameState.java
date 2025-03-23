package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.action.*;
import edu.uob.entity.Container;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import edu.uob.entity.ObjectEntity;
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

    Map<String, Set<GameAction>> actions;
    Map<String, Location> locations;
    Location startLocation;
    GamePlayers players;

    public GameState(File actionsFile, File entitiesFile) throws Exception {
        locations = new HashMap<>();
        this.getEntitiesFromFile(entitiesFile);
        actions = this.getCustomActions(actionsFile);
        actions.putAll(this.getBasicActions());
        players = new GamePlayers(startLocation);
    }

    public String handleCommand(String input) throws Exception {
        Player player = players.getPlayer(input.substring(0, input.indexOf(':')));
        String command = input.substring(input.indexOf(':') + 1);
        Tokeniser tokeniser = new Tokeniser(this, locations, player, command);
        EntityList entities = tokeniser.getEntities();
        GameAction action = tokeniser.getAction(entities);
        StringBuilder sb = new StringBuilder(action.performAction(player, entities));
        return sb.append("\n").toString();
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
        return actions;
    }

    private Set<GameAction> encapsulateActionInSet(GameAction action) {
        Set<GameAction> actionSet = new HashSet<>();
        actionSet.add(action);
        return actionSet;
    }

    private Map<String, Set<GameAction>> getCustomActions(File actionsFile) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getChildNodes();
        int index = 1;
        Element action;
        Map<String, Set<GameAction>> actions = new HashMap<>();

        while ((action = (Element) actionNodes.item(index)) != null) {
            Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
            Set<String> keyPhrases = getKeyPhrases(triggers);
            CustomAction customAction = new CustomAction(locations, action);

            for (String keyPhrase : keyPhrases) {

                if (actions.containsKey(keyPhrase)) {
                    actions.get(keyPhrase).add(customAction);
                }
                else {
                    Set<GameAction> actionSet = new HashSet<>();
                    actionSet.add(customAction);
                    actions.put(keyPhrase, actionSet);
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
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        Graph startLocation = wholeDocument.getSubgraphs().get(0).getSubgraphs().get(0);
        this.startLocation = new Location(startLocation);
        this.startLocation.initialiseAllEntityLocations();
        locations.put(this.startLocation.getName(), this.startLocation);
        wholeDocument.getSubgraphs().get(0).getSubgraphs().remove(0);

        for (Graph graph : wholeDocument.getSubgraphs().get(0).getSubgraphs()) {
            Location location = new Location(graph);
            location.initialiseAllEntityLocations(); // set all entity locations to current location
            locations.put(location.getName(), location);
        }
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
        System.out.println("Start = " + startLocation.getName());
    }

//    public void printActions() {
//
//        for (Map.Entry<String, GameAction> action : actions.entrySet()) {
//            System.out.println(action.getKey().toUpperCase());
//            System.out.println(action.getValue().toString());
//        }
//    }
}
