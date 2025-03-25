package edu.uob.entity;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.Player;

import java.util.*;
import java.util.function.Function;

public class Location extends Container {

    private Map<String, Player> players;
    private Map<String, Character> characters;
    private Map<String, Artefact> artefacts;
    private Map<String, Furniture> furniture;
    private Map<String, Location> paths;

    public Location(Graph graph) throws Exception {
        super(graph.getNodes(false).get(0).getId().getId(),
              graph.getNodes(false).get(0).getAttribute("description"));

        players = new HashMap<>();
        characters = new HashMap<>();
        artefacts = new HashMap<>();
        furniture = new HashMap<>();
        paths = new HashMap<>();

        for (Graph subGraph : graph.getSubgraphs()) {
            Map<String, GameEntity> entities = Location.getEntityData(subGraph);

            if (entities.isEmpty()) continue;

            GameEntity entity = entities.entrySet().iterator().next().getValue();

            if (entity instanceof Character) {
                characters.putAll(Location.downCastMap(Character.class, entities));
            }
            else if (entity instanceof Artefact) {
                artefacts.putAll(Location.downCastMap(Artefact.class, entities));
            }
            else {
                furniture.putAll(Location.downCastMap(Furniture.class, entities));
            }
        }
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getName());
    }

    public void initialiseAllEntityLocations() {
        this.initialiseEntityLocations(characters);
        this.initialiseEntityLocations(artefacts);
        this.initialiseEntityLocations(furniture);
    }

    private <T extends ObjectEntity> void initialiseEntityLocations(Map<String, T> entities) {

        for (Map.Entry<String, T> entry : entities.entrySet()) {
            entry.getValue().setContainer(this);
        }
    }

    public boolean hasNoEntities() {

        if (!characters.isEmpty()) return false;
        if (!artefacts.isEmpty()) return false;
        if (!furniture.isEmpty()) return false;
        return paths.isEmpty();
    }

    public String playersAndEntitiesToString(Player activePlayer) {

        StringBuilder sb = new StringBuilder();
        sb.append(this.entitiesTypeToString(characters));
        sb.append(this.entitiesTypeToString(artefacts));
        sb.append(this.entitiesTypeToString(furniture));
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
        if (characters.containsKey(entityName)) return characters.get(entityName);
        if (artefacts.containsKey(entityName)) return artefacts.get(entityName);
        if (furniture.containsKey(entityName)) return furniture.get(entityName);
        return null;
    }

    public boolean containsEntity(GameEntity entity) {

        if (entity instanceof Character) return characters.containsKey(entity.getName());
        if (entity instanceof Artefact) return artefacts.containsKey(entity.getName());
        if (entity instanceof Furniture) return this.furniture.containsKey(entity.getName());
        else return true; // Any location can be subject/consumed/produced
    }

    public void addEntity(Artefact artefact) {
        artefacts.put(artefact.getName(), artefact);
    }

    public void addEntity(Character character) {
        characters.put(character.getName(), character);
    }

    public void addEntity(Furniture furniture) {
        this.furniture.put(furniture.getName(), furniture);
    }

    public void addEntity(Location path) {
        paths.put(path.getName(), path);
    }

    @Override
    public void moveEntity(Container toLocation, Container... fromLocations) throws Exception {
        throw new Exception(); // location cant be moved
    }

    public void removeEntity(Artefact artefact) {
        artefacts.remove(artefact.getName());
    }

    public void removeEntity(Character character) {
        characters.remove(character.getName());
    }

    public void removeEntity(Furniture furniture) {
        this.furniture.remove(furniture.getName());
    }

    public void removeEntity(Location path) {
        paths.remove(path.getName());
    }

    public Map<String, Location> getPaths() {
        return paths;
    }

    private static <T extends GameEntity> Map<String, T> downCastMap(Class<T> type,
                                                             Map<String, GameEntity> entities) {
        Map<String, T> entitiesCast = new HashMap<>();

        for (Map.Entry<String, GameEntity> entity : entities.entrySet()) {
            entitiesCast.put(entity.getKey(), type.cast(entity.getValue()));
        }
        return entitiesCast;
    }

    private static Map<String, GameEntity> getEntityData(Graph graph) throws Exception {

        return switch (graph.getId().getId()) {
            case "characters" -> Location.getEntities(graph, Location::getCharacter);
            case "artefacts" -> Location.getEntities(graph, Location::getArtefact);
            case "furniture" -> Location.getEntities(graph, Location::getFurniture);
            default -> throw new Exception();
        };
    }

    private static Map<String, GameEntity> getEntities(Graph graph, Function<Node,
                                                                GameEntity> getEntity) {
        Map<String, GameEntity> entities = new HashMap<>();

        for (Node node : graph.getNodes(false)) {
            entities.put(node.getId().getId(), getEntity.apply(node));
        }
        return entities;
    }

    private static GameEntity getCharacter(Node node) {
        return new Character(node.getId().getId(), node.getAttribute("description"));
    }

    private static GameEntity getArtefact(Node node) {
        return new Artefact(node.getId().getId(), node.getAttribute("description"));
    }

    private static GameEntity getFurniture(Node node) {
        return new Furniture(node.getId().getId(), node.getAttribute("description"));
    }

    private <T extends GameEntity> String entitiesToString(Map<String, T> entities) {

        if (entities.isEmpty()) return "Empty\n";

        StringBuilder str = new StringBuilder();

        for (Map.Entry<String, T> entity : entities.entrySet()) {
            str.append(entity.getValue().toString()).append("\n");
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
