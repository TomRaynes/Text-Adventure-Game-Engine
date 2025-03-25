package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameState;
import edu.uob.Player;
import edu.uob.entity.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomAction extends GameAction {

    Map<String, Location> locations;
    private final Element action;
    private final EntityList subjects;
    private final EntityList consumed;
    private final EntityList produced;
    private final String narration;
    private int healthEffect;

    public CustomAction(Map<String, Location> locations, Element action) {
        this.locations = locations;
        this.action = action;
        this.healthEffect = 0;
        subjects = this.getEntities(this.getElement("subjects"));
        consumed = this.getEntities(this.getElement("consumed"));
        produced = this.getEntities(this.getElement("produced"));
        narration = this.getElement("narration").getTextContent();
    }

    public String performAction(Player player, EntityList entities) throws Exception {

        this.checkValidityOfEntities(entities);
        this.checkAvailabilityOfEntities(player);
        this.consumeEntities(player);
        this.produceEntities(player.getLocation());
        String deathMessage = player.updateHealth(healthEffect);
        StringBuilder sb = new StringBuilder(narration);

        if (!deathMessage.isEmpty()) {
            sb.append("\n\n").append(deathMessage);
        }
        return sb.toString();
    }

    private void produceEntities(Location location) throws Exception {

        for (GameEntity entity : produced) {

            if (entity instanceof Location path) {
                location.addEntity(path);
            }
            else {
                ObjectEntity objectEntity = (ObjectEntity) entity;
                objectEntity.moveEntity(location, (Container) null);
            }
        }
    }

    private void consumeEntities(Player player) throws Exception {

        Location location = player.getLocation();
        Inventory inventory = player.getInventory();

        for (GameEntity entity : consumed) {

            if (entity instanceof Location path) {
                location.removeEntity(path);
            }
            else {
                ObjectEntity objectEntity = (ObjectEntity) entity;
                objectEntity.moveEntity(this.getStoreroom(), location, inventory);
            }
        }
    }

    private Location getStoreroom() {
        return locations.get("storeroom");
    }

    private void checkAvailabilityOfEntities(Player player) throws Exception {

        Location location = player.getLocation();
        Inventory inventory = player.getInventory();
        EntityList entities = new EntityList(subjects, consumed);
//        Set<GameEntity> entities = subjects.toSet(); // subject entities of action
//        entities.addAll(consumed.toSet());

        for (GameEntity entity : entities) {

            if (location.containsEntity(entity) || inventory.containsEntity(entity)) {
                continue;
            }
            throw new Exception(); // entity is not in inventory or current location
        }
    }

    private void checkValidityOfEntities(EntityList entities) throws Exception {

        if (entities.isEmpty()) {
            throw new Exception(); // action must contain at least one entity
        }

        Set<GameEntity> extraneousEntities = new HashSet<>();

        for (GameEntity entity : entities) {

            if (!subjects.containsEntity(entity)) {
                extraneousEntities.add(entity);
            }
        }
        if (extraneousEntities.isEmpty()) return;
        throw new Exception();
    }

    private EntityList getEntities(Element element) {

        Node entityNode;
        int index = 0;
        EntityList entities = new EntityList();

        while ((entityNode = element.getElementsByTagName("entity").item(index++)) != null) {

            if (Objects.equals(entityNode.getTextContent(), "health")) {
                this.setHealthEffect(element.getTagName());
                continue;
            }

            GameEntity entity = GameState.getEntityFromLocations(entityNode.getTextContent(), locations);
            if (entity != null) entities.addEntity(entity);
        }
        return entities;
    }

    public EntityList getSubjects() {
        return subjects;
    }

    private void setHealthEffect(String elementType) {

        if (Objects.equals(elementType, "produced")) {
            healthEffect++;
        }
        else healthEffect--;
    }

    private Element getElement(String elementType) {
        return (Element) action.getElementsByTagName(elementType).item(0);
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("\nSUBJECTS:\n").append(subjects.toString()).append("CONSUMED:\n")
                .append(consumed.toString()).append("PRODUCED:\n").append(produced.toString()).append("\n");

        str.append("NARRATION: ").append(narration).append("\nHEALTH: ").append(healthEffect)
                .append("\n_________________\n");

        return str.toString();
    }
}
