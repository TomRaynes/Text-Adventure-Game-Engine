package edu.uob;

import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.*;

final class EntitiesFileTests {

    private GameState state;

    // Test to make sure that the basic entities file is readable
    @Test
    void testBasicEntitiesFileIsReadable() {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(GameServer.joinStrings("config", File.separator,
                                                                      "basic-entities.dot"));
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            //ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            Graph locations = wholeDocument.getSubgraphs().get(0);
            Graph paths = wholeDocument.getSubgraphs().get(1);

            // The locations will always be in the first subgraph
            //ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            Graph firstLocation = locations.getSubgraphs().get(0);
            Node locationDetails = firstLocation.getNodes(false).get(0);
            // Yes, you do need to get the ID twice !
            String locationName = locationDetails.getId().getId();
            assertEquals("cabin", locationName, "First location should have been 'cabin'");

            // The paths will always be in the second subgraph
            //ArrayList<Edge> paths = sections.get(1).getEdges();
            Edge firstPath = paths.getEdges().get(0);
            Node fromLocation = firstPath.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = firstPath.getTarget().getNode();
            String toName = toLocation.getId().getId();
            assertEquals("cabin", fromName, "First path should have been from 'cabin'");
            assertEquals("forest", toName, "First path should have been to 'forest'");

        } catch (FileNotFoundException fnfe) {
            fail("FileNotFoundException was thrown when attempting to read basic entities file");
        } catch (ParseException pe) {
            fail("ParseException was thrown when attempting to read basic entities file");
        }
    }

    @Test
    void testBasic() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "basic-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "basic-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
        Map<String, Location> locations = state.getLocations();
        assertTrue(locations.containsKey("cabin"));
        assertTrue(locations.containsKey("forest"));
        assertTrue(locations.containsKey("cellar"));
        assertTrue(locations.containsKey("storeroom"));
        assertEquals("cabin", state.getStartLocation().getName());
        assertEquals(4, locations.size());

        Location cabin = locations.get("cabin");
        Map<String, GameEntity> entities = cabin.getAllEntities();
        assertTrue(entities.containsKey("axe"));
        assertTrue(entities.containsKey("potion"));
        assertTrue(entities.containsKey("trapdoor"));
        assertTrue(entities.containsKey("forest"));
        assertEquals(4, entities.size());

        Location forest = locations.get("forest");
        entities = forest.getAllEntities();
        assertTrue(entities.containsKey("key"));
        assertTrue(entities.containsKey("tree"));
        assertTrue(entities.containsKey("cabin"));
        assertEquals(3, entities.size());

        Location cellar = locations.get("cellar");
        entities = cellar.getAllEntities();
        assertTrue(entities.containsKey("elf"));
        assertTrue(entities.containsKey("cabin"));
        assertEquals(2, entities.size());

        Location storeroom = locations.get("storeroom");
        entities = storeroom.getAllEntities();
        assertTrue(entities.containsKey("log"));
        assertEquals(1, entities.size());
    }

    @Test
    void testExtended() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "extended-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "extended-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
        Map<String, Location> locations = state.getLocations();
        assertTrue(locations.containsKey("cabin"));
        assertTrue(locations.containsKey("forest"));
        assertTrue(locations.containsKey("cellar"));
        assertTrue(locations.containsKey("riverbank"));
        assertTrue(locations.containsKey("clearing"));
        assertTrue(locations.containsKey("storeroom"));
        assertEquals("cabin", state.getStartLocation().getName());
        assertEquals(6, locations.size());

        Location cabin = locations.get("cabin");
        Map<String, GameEntity> entities = cabin.getAllEntities();
        assertTrue(entities.containsKey("axe"));
        assertTrue(entities.containsKey("potion"));
        assertTrue(entities.containsKey("coin"));
        assertTrue(entities.containsKey("trapdoor"));
        assertTrue(entities.containsKey("forest"));
        assertEquals(5, entities.size());

        Location forest = locations.get("forest");
        entities = forest.getAllEntities();
        assertTrue(entities.containsKey("key"));
        assertTrue(entities.containsKey("tree"));
        assertTrue(entities.containsKey("cabin"));
        assertTrue(entities.containsKey("riverbank"));
        assertEquals(4, entities.size());

        Location riverbank = locations.get("riverbank");
        entities = riverbank.getAllEntities();
        assertTrue(entities.containsKey("horn"));
        assertTrue(entities.containsKey("river"));
        assertTrue(entities.containsKey("forest"));
        assertEquals(3, entities.size());

        Location clearing = locations.get("clearing");
        entities = clearing.getAllEntities();
        assertTrue(entities.containsKey("ground"));
        assertTrue(entities.containsKey("riverbank"));
        assertEquals(2, entities.size());

        Location cellar = locations.get("cellar");
        entities = cellar.getAllEntities();
        assertTrue(entities.containsKey("elf"));
        assertTrue(entities.containsKey("cabin"));
        assertEquals(2, entities.size());

        Location storeroom = locations.get("storeroom");
        entities = storeroom.getAllEntities();
        assertTrue(entities.containsKey("log"));
        assertTrue(entities.containsKey("lumberjack"));
        assertTrue(entities.containsKey("shovel"));
        assertTrue(entities.containsKey("gold"));
        assertTrue(entities.containsKey("hole"));
        assertEquals(5, entities.size());
    }

    @Test
    void testNoStoreroom() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "no-storeroom-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "no-storeroom-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
        Map<String, Location> locations = state.getLocations();
        assertTrue(locations.containsKey("cabin"));
        assertTrue(locations.containsKey("forest"));
        assertTrue(locations.containsKey("cellar"));
        assertTrue(locations.containsKey("storeroom"));
        assertEquals("cabin", state.getStartLocation().getName());
        assertEquals(4, locations.size());

        Location cabin = locations.get("cabin");
        Map<String, GameEntity> entities = cabin.getAllEntities();
        assertTrue(entities.containsKey("axe"));
        assertTrue(entities.containsKey("potion"));
        assertTrue(entities.containsKey("trapdoor"));
        assertTrue(entities.containsKey("forest"));
        assertEquals(4, entities.size());

        Location forest = locations.get("forest");
        entities = forest.getAllEntities();
        assertTrue(entities.containsKey("key"));
        assertTrue(entities.containsKey("tree"));
        assertTrue(entities.containsKey("cabin"));
        assertEquals(3, entities.size());

        Location cellar = locations.get("cellar");
        entities = cellar.getAllEntities();
        assertTrue(entities.containsKey("elf"));
        assertTrue(entities.containsKey("cabin"));
        assertEquals(2, entities.size());

        Location storeroom = locations.get("storeroom");
        entities = storeroom.getAllEntities();
        assertTrue(entities.isEmpty());
    }
}
