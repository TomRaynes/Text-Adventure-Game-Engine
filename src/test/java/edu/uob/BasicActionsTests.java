package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicActionsTests {

    GameState state;

    @BeforeEach
    public void initialiseState() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "testBasic-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "testBasic-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);

    }

    private String handleCommand(String playerName, String command) {
        command = GameServer.joinStrings(playerName, ": ", command);
        return state.handleCommand(command);
    }

    @Test
    void testLook() {
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a log cabin in the woods. You can see:
                          A razor sharp axe
                          Some medicine
                          Magic potion
                          Wooden trapdoor
                          A dark forest
                          """;
        assertEquals(expected, response);

        // add second player
        response = this.handleCommand("David", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   Roger, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // add third player
        response = this.handleCommand("Richard", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   David, a fellow adventurer
                   Roger, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // add fourth player
        response = this.handleCommand("Nick", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   David, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // different locations
        this.handleCommand("Roger", "goto forest");
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   Brass key
                   A big tree
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        this.handleCommand("Roger", "get key");
        this.handleCommand("Roger", "goto cabin");
        this.handleCommand("Roger", "unlock trapdoor");
        this.handleCommand("Roger", "goto cellar");
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dusty cellar. You can see:
                   Angry Elf
                   A log cabin in the woods
                   An empty room
                   """;
        assertEquals(expected, response);

        // test decorated commands
        response = this.handleCommand("Roger", "look around");
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "look around the room");
        assertEquals(expected, response);

        // test invalid commands
        response = this.handleCommand("Roger", "What can I see when I look around?");
        expected = "ERROR: Incorrect order of tokens in command\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "look around the cellar");
        expected = "ERROR: An entity was referenced in LOOK command\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "look get");
        expected = "ERROR: Ambiguous command could be matched to multiple actions\n";
        assertEquals(expected, response);

        // empty location
        this.handleCommand("Roger", "goto emptyroom");
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in an empty room. Nothing can be seen.
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testInventory() {

        String response = this.handleCommand("Roger", "inv");
        String expected = "Your inventory is empty\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "Inventory");
        assertEquals(expected, response);

        this.handleCommand("Roger", "get axe");
        response = this.handleCommand("Roger", "inv");
        expected = """
                   Your inventory contains:
                   A razor sharp axe
                   """;
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "inventory");
        assertEquals(expected, response);

        this.handleCommand("Roger", "get potion");
        response = this.handleCommand("Roger", "INV");
        expected = """
                   Your inventory contains:
                   A razor sharp axe
                   Magic potion
                   """;
        assertEquals(expected, response);

        // test decorated command
        response = this.handleCommand("Roger", "inventory show");
        expected = """
                   Your inventory contains:
                   A razor sharp axe
                   Magic potion
                   """;
        assertEquals(expected, response);

        // incorrect ordering
        response = this.handleCommand("Roger", "show my inventory");
        expected = "ERROR: Incorrect order of tokens in command\n";
        assertEquals(expected, response);

        // invalid command
        response = this.handleCommand("Roger", "get inv");
        expected = "ERROR: Ambiguous command could be matched to multiple actions\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "inv axe");
        expected = "ERROR: An entity was referenced in INVENTORY command\n";
        assertEquals(expected, response);
    }

    @Test
    void testGet() {

        String response = this.handleCommand("Roger", "inv");
        String expected = "Your inventory is empty\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "get axe");
        expected = "Axe added to inventory\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "inv");
        expected = """
                   Your inventory contains:
                   A razor sharp axe
                   """;
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "get axe");
        expected = "ERROR: Axe is already in your inventory\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "get sword");
        expected = "ERROR: No entity referenced in GET command\n";
        assertEquals(expected, response);

        // test get from another players inventory
        response = this.handleCommand("David", "get axe");
        expected = "ERROR: Axe is in another player's inventory\n";
        assertEquals(expected, response);

        // test get player
        response = this.handleCommand("Roger", "get David");
        expected = "ERROR: No entity referenced in GET command\n";
        assertEquals(expected, response);

        // test get location
        response = this.handleCommand("Roger", "get cabin");
        expected = "ERROR: The cabin cannot be moved\n";
        assertEquals(expected, response);

        // test get furniture
        response = this.handleCommand("Roger", "get trapdoor");
        expected = "ERROR: Trapdoor cannot be added to inventory\n";
        assertEquals(expected, response);

        // test get location
        response = this.handleCommand("Roger", "get cabin");
        expected = "ERROR: The cabin cannot be moved\n";
        assertEquals(expected, response);

        // test get character
        this.handleCommand("Roger", "goto forest");
        this.handleCommand("Roger", "get key");
        this.handleCommand("Roger", "goto cabin");
        this.handleCommand("Roger", "unlock trapdoor");
        this.handleCommand("Roger", "goto cellar");
        response = this.handleCommand("Roger", "get elf");
        expected = "ERROR: Elf cannot be added to inventory\n";
        assertEquals(expected, response);

        // no referenced entity
        response = this.handleCommand("Roger", "get");
        expected = "ERROR: No entity referenced in GET command\n";
        assertEquals(expected, response);

        // entity in wrong location
        this.handleCommand("Roger", "goto forest");
        response = this.handleCommand("Roger", "get potion");
        expected = "ERROR: Potion is in another location\n";
        assertEquals(expected, response);
    }

    @Test
    void testDrop() {

        String response = this.handleCommand("Roger", "drop axe");
        String expected = "ERROR: Axe is not in your inventory\n";
        assertEquals(expected, response);

        this.handleCommand("Roger", "get axe");
        // duplicate entity
        response = this.handleCommand("Roger", "drop axe axe");
        expected = "Axe dropped from inventory\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "drop axe");
        expected = "ERROR: Axe is not in your inventory\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "inv");
        expected = "Your inventory is empty\n";
        assertEquals(expected, response);

        // drop furniture (not in inventory)
        response = this.handleCommand("Roger", "drop trapdoor");
        expected = "ERROR: Trapdoor is not in your inventory\n";
        assertEquals(expected, response);

        //drop location (not in inventory)
        response = this.handleCommand("Roger", "drop forest");
        expected = "ERROR: The forest cannot be moved\n";
        assertEquals(expected, response);

        //drop character (not in inventory)
        response = this.handleCommand("Roger", "drop elf");
        expected = "ERROR: Elf is not in your inventory\n";
        assertEquals(expected, response);

        //drop location (not in inventory)
        response = this.handleCommand("Roger", "drop cabin");
        expected = "ERROR: The cabin cannot be moved\n";
        assertEquals(expected, response);
    }

    @Test
    void testGoto() {

        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a log cabin in the woods. You can see:
                          A razor sharp axe
                          Some medicine
                          Magic potion
                          Wooden trapdoor
                          A dark forest
                          """;
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "goto forest");
        expected = "You enter the forest\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   Brass key
                   A big tree
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // test goto location with no path
        response = this.handleCommand("Roger", "goto cellar");
        expected = "ERROR: There is no path from the forest to the cellar\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   Brass key
                   A big tree
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // test goto current location
        response = this.handleCommand("Roger", "goto forest");
        expected = "ERROR: You are already in the forest\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   Brass key
                   A big tree
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // test decorated goto
        response = this.handleCommand("Roger", "goto the cabin in the woods");
        expected = "You enter the cabin\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // test no entity
        response = this.handleCommand("Roger", "goto");
        expected = "ERROR: No entity referenced in GOTO command\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // test goto two locations
        response = this.handleCommand("Roger", "goto cabin forest");
        expected = "ERROR: Multiple entities were referenced in GOTO command\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // test goto non location
        response = this.handleCommand("Roger", "goto axe");
        expected = "ERROR: Axe is not a location\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testHealth() {
        this.handleCommand("Roger", "get potion");
        this.handleCommand("Roger", "get axe");
        this.handleCommand("Roger", "get medicine");
        this.handleCommand("Roger", "goto forest");
        this.handleCommand("Roger", "get key");
        this.handleCommand("Roger", "goto cabin");
        this.handleCommand("Roger", "unlock with key");
        this.handleCommand("Roger", "goto cellar");

        String response = this.handleCommand("Roger", "health");
        String expected = "Your health level is 3\n";
        assertEquals(expected, response);

        // 'hit' elf, -1 health
        this.handleCommand("Roger", "hit elf");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 2\n";
        assertEquals(expected, response);

        // 'drink' potion, +1 health
        this.handleCommand("Roger", "drink potion");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 3\n";
        assertEquals(expected, response);

        // 'fight' potion, -2 health
        this.handleCommand("Roger", "fight elf");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 1\n";
        assertEquals(expected, response);

        // 'take' medicine, +2 health
        this.handleCommand("Roger", "take medicine");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 3\n";
        assertEquals(expected, response);

        // test health doesnt go beyond 3
        response = this.handleCommand("Roger", "drink potion");
        expected = "You drink the potion and your health improves\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 3\n";
        assertEquals(expected, response);

        // test player dies when health reaches zero
        this.handleCommand("Roger", "hit elf");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 2\n";
        assertEquals(expected, response);
        this.handleCommand("Roger", "hit elf");
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 1\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "hit elf");
        expected = """
                   You attack the elf, but he fights back and you lose some health
                   
                   You died and lost all of your items. You must return to the start of the game
                   """;
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "health");
        expected = "Your health level is 3\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   Wooden trapdoor
                   A dusty cellar
                   A dark forest
                   """;
        assertEquals(expected, response);

        // check artefacts were dropped
        this.handleCommand("Roger", "goto cellar");
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dusty cellar. You can see:
                   A razor sharp axe
                   Angry Elf
                   Some medicine
                   Magic potion
                   A log cabin in the woods
                   An empty room
                   """;
        assertEquals(expected, response);

        // test decorated health command
        response = this.handleCommand("Roger", "health level?");
        expected = "Your health level is 3\n";
        assertEquals(expected, response);

        // test invalid command
        response = this.handleCommand("Roger", "What is my health level?");
        expected = "ERROR: Incorrect order of tokens in command\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "get health");
        expected = "ERROR: Ambiguous command could be matched to multiple actions\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "health of elf");
        expected = "ERROR: An entity was referenced in HEALTH command\n";
        assertEquals(expected, response);
    }
}
