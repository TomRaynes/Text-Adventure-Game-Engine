package edu.uob.entity;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.GameServer;
import edu.uob.Player;
import edu.uob.STAGException;

import java.util.*;
import java.util.function.Function;

public class Location extends Container {

    private Map<String, Player> players;
    private Map<String, GameEntity> entities; // Artefacts, Furniture, Characters
    private Map<String, Location> paths;

    public Location(Graph graph) throws Exception {
        super(graph.getNodes(false).get(0).getId().getId().toLowerCase(),
              graph.getNodes(false).get(0).getAttribute("description"));

        this.mallocFields();

        for (Graph subGraph : graph.getSubgraphs()) {
            entities.putAll(Location.getEntityData(subGraph));
        }
    }

    public Location(String name, String description) {
        super (name, description);
        this.mallocFields();
    }

    private void mallocFields() {
        players = new HashMap<>();
        entities = new HashMap<>();
        paths = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getName());
    }

    public void initialiseAllEntityLocations() {

        for (GameEntity entity : entities.values()) {
            entity.setContainer(this);
        }
    }

    public boolean isEmpty() {
        if (!entities.isEmpty()) return false;
        return paths.isEmpty();
    }

    public String playersAndEntitiesToString(Player activePlayer) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.entitiesTypeToString(entities));
        sb.append(this.entitiesTypeToString(paths));

        for (Player player : players.values()) {
            if (player == activePlayer) continue;
            sb.append(player.getName()).append(", a fellow adventurer\n");
        }
        String str = sb.toString();
        return str.substring(0, str.length() - 1);
    }

    private <T extends GameEntity> String entitiesTypeToString(Map<String, T> entities) {

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, T> entry : entities.entrySet()) {
            sb.append(entry.getValue().getDescription()).append("\n");
        }
        return sb.toString();
    }

    public GameEntity getEntity(String entityName) {
        if (Objects.equals(this.getName(), entityName)) return this;
        return entities.get(entityName);
    }

    public boolean containsEntity(GameEntity entity) {

        if (entity instanceof Location) {
            return Objects.equals(entity.getName(), this.getName());
        }
        return entities.containsKey(entity.getName());
    }

    public void addEntity(Artefact artefact) {
        entities.put(artefact.getName(), artefact);
    }

    public void addEntity(Character character) {
        entities.put(character.getName(), character);
    }

    public void addEntity(Furniture furniture) {
        entities.put(furniture.getName(), furniture);
    }

    public void addEntity(Location path) {
        paths.put(path.getName(), path);
    }

    public void moveEntity(Container toLocation, Container fromLocation) throws Exception {
        throw new STAGException.TryingToMoveContainerException(this);
    }

    public void removeEntity(Artefact artefact) {
        entities.remove(artefact.getName());
    }

    public void removeEntity(Character character) {
        entities.remove(character.getName());
    }

    public void removeEntity(Furniture furniture) {
        this.entities.remove(furniture.getName());
    }

    public void removeEntity(Location path) {
        paths.remove(path.getName());
    }

    public Map<String, Location> getPaths() {
        return paths;
    }

    private static Map<String, GameEntity> getEntityData(Graph graph) throws Exception {

        switch (graph.getId().getId()) {
            case "characters": return Location.getEntities(graph, Location::addCharacter);
            case "artefacts": return Location.getEntities(graph, Location::addArtefact);
            case "furniture": return Location.getEntities(graph, Location::addFurniture);
            default: throw new STAGException.MalformedEntitiesFileException();
        }
    }

    private static Map<String, GameEntity> getEntities(Graph graph, Function<Node,
                                                                GameEntity> getEntity) {
        Map<String, GameEntity> entities = new HashMap<>();

        for (Node node : graph.getNodes(false)) {
            entities.put(node.getId().getId(), getEntity.apply(node));
        }
        return entities;
    }

    private static GameEntity addCharacter(Node node) {
        return new Character(node.getId().getId().toLowerCase(),
                             node.getAttribute("description"));
    }

    private static GameEntity addArtefact(Node node) {
        return new Artefact(node.getId().getId().toLowerCase(),
                            node.getAttribute("description"));
    }

    private static GameEntity addFurniture(Node node) {
        return new Furniture(node.getId().getId().toLowerCase(),
                             node.getAttribute("description"));
    }

    //Testing method
    public Map<String, GameEntity> getAllEntities() {
        Map<String, GameEntity> entities = new HashMap<>(this.entities);

        for (Location path : paths.values()) {
            entities.put(path.getName(), path);
        }
        return entities;
    }

    private <T extends GameEntity> String entitiesToString(Map<String, T> entities) {
        StringBuilder str = new StringBuilder();

        for (GameEntity entity : entities.values()) {
            str.append(entity.getName()).append("\n");
        }
        return str.toString();
    }

    private String pathsToString() {

        if (paths.isEmpty()) return "Empty\n";

        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, Location> location : paths.entrySet()) {
            str.append(location.getValue().getName()).append("\n");
        }
        return str.toString();
    }

    public String toString() {

        return this.getName();

//        return super.toString() + "\n\n" +
//                "CHARACTERS:\n" +
//                entitiesToString(characters) + "\n" +
//                "ARTEFACTS:\n" +
//                entitiesToString(artefacts) + "\n" +
//                "FURNITURE:\n" +
//                entitiesToString(furniture) + "\n" +
//                "PATHS:\n" +
//                pathsToString() + "\n";
    }
}
