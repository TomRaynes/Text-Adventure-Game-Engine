package edu.uob.action;

import edu.uob.*;
import edu.uob.entity.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.Objects;

public class CustomAction extends GameAction {

    private final Map<String, Location> locations;
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
        this.checkAvailabilityOfEntities(player);
        this.checkValidityOfEntities(entities);
        this.consumeEntities(player);
        this.produceEntities(player.getLocation());
        String deathMessage = player.updateHealth(healthEffect);

        if (deathMessage.isEmpty()) {
            return narration;
        }
        return GameServer.joinStrings(narration, "\n\n", deathMessage);
    }

    private void produceEntities(Location location) throws Exception {
        for (GameEntity entity : produced) {
            if (entity instanceof Location path) {
                location.addEntity(path);
            }
            else {
                entity.moveEntity(location, null);
            }
        }
    }

    private void consumeEntities(Player player) throws Exception {
        for (GameEntity entity : consumed) {
            if (entity instanceof Location path) {
                player.getLocation().removeEntity(path);
            }
            else {
                entity.moveEntity(this.getStoreroom(), entity.getContainer());
            }
        }
    }

    private Location getStoreroom() {
        return locations.get("storeroom");
    }

    public boolean isPerformable(Player player) {
        try {
            this.checkAvailabilityOfEntities(player);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    private void checkAvailabilityOfEntities(Player player) throws Exception {

        Location location = player.getLocation();
        Inventory inventory = player.getInventory();
        EntityList entities = new EntityList(subjects, consumed);

        // locations will never be in current location or inventory
        for (GameEntity entity : entities) {

            if (entity instanceof Location) {
                entities.removeEntity(entity);
            }
        }
        for (GameEntity entity : entities) {

            if (location.containsEntity(entity) || inventory.containsEntity(entity)) {
                continue;
            }
            // entity is not in inventory or current location
            if (!(entity instanceof Artefact)) {
                throw new STAGException.ActionInWrongLocationException();
            }
            throw new STAGException.UnavailableEntityException();
        }
        // if produced entity is in another players inventory
        if (produced.containsEntityInForeignInventory(inventory)) {
            throw new STAGException.ProducedEntityInForeignInventoryException();
        }
    }

    private void checkValidityOfEntities(EntityList entities) throws Exception {

        if (entities.isEmpty()) {  // action must contain at least one entity
            throw new STAGException.NoSpecifiedEntityException(null);
        }

        for (GameEntity entity : entities) {

            if (!subjects.containsEntity(entity)) {
                throw new STAGException.ExtraneousEntityException();
            }
        }
    }

    private EntityList getEntities(Element element) {

        Node entityNode;
        int index = 0;
        EntityList entities = new EntityList();

        while ((entityNode = element.getElementsByTagName("entity").item(index++)) != null) {
            String entityName = entityNode.getTextContent().toLowerCase();

            if (Objects.equals(entityName, "health")) {
                this.setHealthEffect(element.getTagName());
                continue;
            }
            GameEntity entity = GameState.getEntityFromLocations(entityName, locations);
            if (entity != null) entities.addEntity(entity);
        }
        return entities;
    }

    public EntityList getSubjects() {
        return subjects;
    }

    private void setHealthEffect(String elementType) {

        if (Objects.equals(elementType.toLowerCase(), "produced")) {
            healthEffect++;
        }
        else healthEffect--;
    }

    private Element getElement(String elementType) {
        return (Element) action.getElementsByTagName(elementType).item(0);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("SUBJECTS: ").append(subjects.toString());
        sb.append("\nCONSUMED: ").append(consumed.toString());
        sb.append("\nPRODUCED: ").append(produced.toString());
        sb.append("\nNARRATION: ").append(narration);
        sb.append("\nHEALTH: ").append(healthEffect);
        return sb.toString();
    }
}
