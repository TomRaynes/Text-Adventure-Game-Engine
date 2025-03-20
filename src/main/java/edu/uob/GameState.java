package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import edu.uob.entity.Location;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class GameState {

    Map<String, Location> locations;
    Location startLocation;

    public GameState(File entitiesFile) throws Exception {
        locations = new HashMap<>();
        this.getEntities(entitiesFile);
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
}
