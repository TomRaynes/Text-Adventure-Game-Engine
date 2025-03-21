package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Map;

public class CustomAction extends GameAction {

    private final Element action;
    private final Map<String, Location> locations;
    private final EntityList subjects;
    private final EntityList consumed;
    private final EntityList produced;
    private final String narration;

    public CustomAction(Element action, Map<String, Location> locations) {
        this.locations = locations;
        this.action = action;
        subjects = this.getEntities(this.getElement("subjects"));
        consumed = this.getEntities(this.getElement("consumed"));
        produced = this.getEntities(this.getElement("produced"));
        narration = this.getElement("narration").getTextContent();
    }

    private EntityList getEntities(Element element) {

        Node entityNode;
        int index = 0;
        EntityList entities = new EntityList();

        while ((entityNode = element.getElementsByTagName("entity").item(index++)) != null) {

            GameEntity entity = this.getEntityFromLocations(entityNode);
            if (entity != null) entities.addEntity(entity);
        }
        return entities;
    }

    private Element getElement(String elementType) {
        return (Element) action.getElementsByTagName(elementType).item(0);
    }

    private GameEntity getEntityFromLocations(Node node) {
        String entityName = node.getTextContent();
        GameEntity entity;

        for (Map.Entry<String, Location> location : locations.entrySet()) {
            entity = location.getValue().getEntity(entityName);

            if (entity != null) return entity;
        }
        return null;
    }

    public String toString() {

        StringBuilder str = new StringBuilder();

        str.append("\nSUBJECTS:\n").append(subjects.toString()).append("CONSUMED:\n")
                .append(consumed.toString()).append("PRODUCED:\n").append(produced.toString()).append("\n");

        str.append("NARRATION: ").append(narration).append("\n_________________\n");

        return str.toString();
    }
}
