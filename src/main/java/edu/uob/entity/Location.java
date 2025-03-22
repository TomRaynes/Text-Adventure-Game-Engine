package edu.uob.entity;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Location extends GameEntity {

    private Map<String, Character> characters;
    private Map<String, Artefact> artefacts;
    private Map<String, Furniture> furniture;
    private Map<String, Location> paths;

    public Location(Graph graph) throws Exception {
        super(graph.getNodes(false).get(0).getId().getId(),
              graph.getNodes(false).get(0).getAttribute("description"));

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

    public boolean hasNoEntities() {

        if (!characters.isEmpty()) return false;
        if (!artefacts.isEmpty()) return false;
        if (!furniture.isEmpty()) return false;
        return paths.isEmpty();
    }

    public String EntitiesToString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.entitiesTypeToString(characters));
        sb.append(this.entitiesTypeToString(artefacts));
        sb.append(this.entitiesTypeToString(furniture));
        sb.append(this.entitiesTypeToString(paths));
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

    public GameEntity getEntity(String name) {
        if (Objects.equals(this.getName(), name)) return this;
        if (characters.containsKey(name)) return characters.get(name);
        if (artefacts.containsKey(name)) return artefacts.get(name);
        if (furniture.containsKey(name)) return furniture.get(name);
        return null;
    }

    public void addArtefact(Artefact artefact) {
        artefacts.put(artefact.getName(), artefact);
    }

    public boolean removeArtefact(Artefact artefact) {
        return artefacts.remove(artefact.getName()) != null;
    }

    public boolean locationContains(Character character) {
        return characters.containsValue(character);
    }

    public boolean locationContains(Artefact artefact) {
        return artefacts.containsValue(artefact);
    }

    public boolean locationContains(Furniture furniture) {
        return this.furniture.containsValue(furniture);
    }

    public void addPath(Location location) {
        paths.put(location.getName(), location);
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

        return super.toString() + "\n\n" +
                "CHARACTERS:\n" +
                entitiesToString(characters) + "\n" +
                "ARTEFACTS:\n" +
                entitiesToString(artefacts) + "\n" +
                "FURNITURE:\n" +
                entitiesToString(furniture) + "\n" +
                "PATHS:\n" +
                pathsToString() + "\n";
    }
}
