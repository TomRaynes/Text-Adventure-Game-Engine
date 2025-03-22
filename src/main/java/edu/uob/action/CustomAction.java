package edu.uob.action;

import edu.uob.EntityList;
import edu.uob.GameState;
import edu.uob.entity.GameEntity;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Objects;

public class CustomAction extends GameAction {

    GameState state;
    private final Element action;
    private final EntityList subjects;
    private final EntityList consumed;
    private final EntityList produced;
    private final String narration;
    private int healthEffect;

    public CustomAction(GameState state, Element action) {
        this.state = state;
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

            if (Objects.equals(entityNode.getTextContent(), "health")) {
                this.setHealthEffect(element.getTagName());
                continue;
            }
            else healthEffect = 0;

            GameEntity entity = state.getEntityFromLocations(entityNode.getTextContent());
            if (entity != null) entities.addEntity(entity);
        }
        return entities;
    }

    private void setHealthEffect(String elementType) {

        if (Objects.equals(elementType, "produced")) {
            healthEffect = 1;
        }
        else healthEffect = -1;
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
