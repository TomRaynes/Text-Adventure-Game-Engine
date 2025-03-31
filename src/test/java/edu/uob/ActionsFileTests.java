package edu.uob;

import edu.uob.action.CustomAction;
import edu.uob.action.GameAction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
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

        CustomAction open = this.getActionFromSet(actions.get("open"));
        CustomAction unlock = this.getActionFromSet(actions.get("unlock"));
        String actionString = """
                              SUBJECTS: key, trapdoor
                              CONSUMED: key
                              PRODUCED: cellar
                              NARRATION: You unlock the trapdoor and see steps leading down into a cellar
                              HEALTH: 0""";
        assertEquals(actionString, open.toString());
        assertEquals(actionString, unlock.toString());

        CustomAction chop = this.getActionFromSet(actions.get("chop"));
        CustomAction cut = this.getActionFromSet(actions.get("cut"));
        CustomAction cutdown = this.getActionFromSet(actions.get("cutdown"));
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

    @Test
    void testExtended() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "extended-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "extended-entities.dot")).toAbsolutePath().toFile();
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
        assertTrue(actions.containsKey("cut down"));
        assertTrue(actions.containsKey("drink"));
        assertTrue(actions.containsKey("fight"));
        assertTrue(actions.containsKey("hit"));
        assertTrue(actions.containsKey("attack"));
        assertTrue(actions.containsKey("pay"));
        assertTrue(actions.containsKey("bridge"));
        assertTrue(actions.containsKey("dig"));
        assertTrue(actions.containsKey("blow"));
        assertEquals(20, actions.size());

        CustomAction open = this.getActionFromSet(actions.get("open"));
        CustomAction unlock = this.getActionFromSet(actions.get("unlock"));
        String actionString = """
                              SUBJECTS: key, trapdoor
                              CONSUMED: key
                              PRODUCED: cellar
                              NARRATION: You unlock the door and see steps leading down into a cellar
                              HEALTH: 0""";
        assertEquals(actionString, open.toString());
        assertEquals(actionString, unlock.toString());

        CustomAction chop = this.getActionFromSet(actions.get("chop"));
        CustomAction cut = this.getActionFromSet(actions.get("cut"));
        CustomAction cutDown = this.getActionFromSet(actions.get("cut down"));
        actionString = """
                              SUBJECTS: axe, tree
                              CONSUMED: tree
                              PRODUCED: log
                              NARRATION: You cut down the tree with the axe
                              HEALTH: 0""";
        assertEquals(actionString, chop.toString());
        assertEquals(actionString, cut.toString());
        assertEquals(actionString, cutDown.toString());

        CustomAction drink = this.getActionFromSet(actions.get("drink"));
        actionString = """
                              SUBJECTS: potion
                              CONSUMED: potion
                              PRODUCED:\s
                              NARRATION: You drink the potion and your health improves
                              HEALTH: 1""";
        assertEquals(actionString, drink.toString());

        CustomAction fight = this.getActionFromSet(actions.get("fight"));
        CustomAction hit = this.getActionFromSet(actions.get("hit"));
        CustomAction attack = this.getActionFromSet(actions.get("attack"));
        actionString = """
                              SUBJECTS: elf
                              CONSUMED:\s
                              PRODUCED:\s
                              NARRATION: You attack the elf, but he fights back and you lose some health
                              HEALTH: -1""";
        assertEquals(actionString, fight.toString());
        assertEquals(actionString, hit.toString());
        assertEquals(actionString, attack.toString());

        CustomAction pay = this.getActionFromSet(actions.get("pay"));
        actionString = """
                              SUBJECTS: coin, elf
                              CONSUMED: coin
                              PRODUCED: shovel
                              NARRATION: You pay the elf your silver coin and he produces a shovel
                              HEALTH: 0""";
        assertEquals(actionString, pay.toString());

        CustomAction bridge = this.getActionFromSet(actions.get("bridge"));
        actionString = """
                              SUBJECTS: log, river
                              CONSUMED: log
                              PRODUCED: clearing
                              NARRATION: You bridge the river with the log and can now reach the other side
                              HEALTH: 0""";
        assertEquals(actionString, bridge.toString());

        CustomAction dig = this.getActionFromSet(actions.get("dig"));
        actionString = """
                              SUBJECTS: ground, shovel
                              CONSUMED: ground
                              PRODUCED: gold, hole
                              NARRATION: You dig into the soft ground and unearth a pot of gold !!!
                              HEALTH: 0""";
        assertEquals(actionString, dig.toString());

        CustomAction blow = this.getActionFromSet(actions.get("blow"));
        actionString = """
                              SUBJECTS: horn
                              CONSUMED:\s
                              PRODUCED: lumberjack
                              NARRATION: You blow the horn and as if by magic, a lumberjack appears !
                              HEALTH: 0""";
        assertEquals(actionString, blow.toString());
    }

    @Test
    void testDuplicateTriggers() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "duplicate-trigger-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "duplicate-trigger-entities.dot")).toAbsolutePath().toFile();
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
        assertEquals(9, actions.size());

        String actionString1 = """
                             SUBJECTS: key, trapdoor
                             CONSUMED: key
                             PRODUCED: cellar
                             NARRATION: You unlock the trapdoor and see steps leading down into a cellar
                             HEALTH: 0""";

        String actionString2 = """
                             SUBJECTS: door, housekey
                             CONSUMED: housekey
                             PRODUCED: house
                             NARRATION: You unlock the door to the house
                             HEALTH: 2""";

        Set<GameAction> open = actions.get("open");
        assertEquals(2, open.size());
        Iterator<GameAction> openActions = open.iterator();
        String action1Actual = openActions.next().toString();
        String action2Actual = openActions.next().toString();

        if (!Objects.equals(actionString1, action1Actual)) {
            assertEquals(actionString2, action1Actual);
        }
        if (!Objects.equals(actionString1, action2Actual)) {
            assertEquals(actionString2, action2Actual);
        }

        Set<GameAction> unlock = actions.get("unlock");
        assertEquals(2, open.size());
        Iterator<GameAction> unlockActions = unlock.iterator();
        action1Actual = unlockActions.next().toString();
        action2Actual = unlockActions.next().toString();

        if (!Objects.equals(actionString1, action1Actual)) {
            assertEquals(actionString2, action1Actual);
        }
        if (!Objects.equals(actionString1, action2Actual)) {
            assertEquals(actionString2, action2Actual);
        }
    }
}
