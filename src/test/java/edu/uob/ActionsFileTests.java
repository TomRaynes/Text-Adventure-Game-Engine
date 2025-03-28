package edu.uob;

import edu.uob.action.CustomAction;
import edu.uob.action.GameAction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;

final class ActionsFileTests {

    private GameState state;

    private CustomAction getActionFromSet(Set<GameAction> actionSet) {
        return (CustomAction) actionSet.iterator().next();
    }

    // Test to make sure that the basic actions file is readable
    @Test
    void testBasicActionsFileIsReadable() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(GameServer.joinStrings("config", File.separator,
                                                                     "basic-actions.xml"));
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
            Element firstAction = (Element) actions.item(1);
            Element triggers = (Element) firstAction.getElementsByTagName("triggers").item(0);
            // Get the first trigger phrase
            String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
            assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
        } catch (ParserConfigurationException pce) {
            fail("ParserConfigurationException was thrown when attempting to read basic actions file");
        } catch (SAXException saxe) {
            fail("SAXException was thrown when attempting to read basic actions file");
        } catch (IOException ioe) {
            fail("IOException was thrown when attempting to read basic actions file");
        }
    }

    @Test
    void testBasic() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "basic-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "basic-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
        Map<String, Set<GameAction>> actions = state.getActions();

        assertTrue(actions.containsKey("inv"));
        assertTrue(actions.containsKey("inventory"));
        assertTrue(actions.containsKey("get"));
        assertTrue(actions.containsKey("drop"));
        assertTrue(actions.containsKey("look"));
        assertTrue(actions.containsKey("goto"));
        assertTrue(actions.containsKey("health"));
        assertTrue(actions.containsKey("open"));
        assertTrue(actions.containsKey("unlock"));
        assertTrue(actions.containsKey("chop"));
        assertTrue(actions.containsKey("cut"));
        assertTrue(actions.containsKey("cutdown"));
        assertEquals(12, actions.size());

        CustomAction open = getActionFromSet(actions.get("open"));
        CustomAction unlock = getActionFromSet(actions.get("unlock"));
        String actionString = """
                              SUBJECTS: key, trapdoor
                              CONSUMED: key
                              PRODUCED: cellar
                              NARRATION: You unlock the trapdoor and see steps leading down into a cellar
                              HEALTH: 0""";
        assertEquals(actionString, open.toString());
        assertEquals(actionString, unlock.toString());

        CustomAction chop = getActionFromSet(actions.get("chop"));
        CustomAction cut = getActionFromSet(actions.get("cut"));
        CustomAction cutdown = getActionFromSet(actions.get("cutdown"));
        actionString = """
                              SUBJECTS: axe, tree
                              CONSUMED: tree
                              PRODUCED: log
                              NARRATION: You cut down the tree with the axe
                              HEALTH: 0""";
        assertEquals(actionString, chop.toString());
        assertEquals(actionString, cut.toString());
        assertEquals(actionString, cutdown.toString());

    }
}
