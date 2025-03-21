package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.entity.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {

    Set<GameAction> actions;
    Map<String, Location> locations;
    Location startLocation;

    public GameState(File actionsFile, File entitiesFile) throws Exception {
        locations = new HashMap<>();
        this.getEntities(entitiesFile);
        actions = this.getActions(actionsFile);
    }

    private Set<GameAction> getActions(File actionsFile) throws Exception {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionsFile);
        Element root = document.getDocumentElement();
        NodeList actionNodes = root.getChildNodes();
        int index = 1;
        Element action;
        Set<GameAction> actions = new HashSet<>();

        while ((action = (Element) actionNodes.item(index)) != null) {
            actions.add(new GameAction(action, locations));
            index+=2;
        }
        return actions;
    }

    private void getEntities(File entitiesFile) throws Exception {

        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        Graph startLocation = wholeDocument.getSubgraphs().get(0).getSubgraphs().get(0);
        this.startLocation = new Location(startLocation);
        locations.put(this.startLocation.getName(), this.startLocation);
        wholeDocument.getSubgraphs().get(0).getSubgraphs().remove(0);

        for (Graph graph : wholeDocument.getSubgraphs().get(0).getSubgraphs()) {
            Location location = new Location(graph);
            locations.put(location.getName(), location);
        }
        for (Edge edge : wholeDocument.getSubgraphs().get(1).getEdges()) {
            Location fromLocation = locations.get(edge.getSource().getNode().getId().getId());
            Location toLocation = locations.get(edge.getTarget().getNode().getId().getId());
            fromLocation.addPath(toLocation);
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

    public void printActions() {

        for (GameAction action : actions) {
            System.out.println(action.toString());
        }
    }
}
