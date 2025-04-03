package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomActionTests {

    GameState state;

    @BeforeEach
    public void initialiseState() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "testCustom-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "testCustom-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
        this.handleCommand("Roger", "get axe");
        this.handleCommand("Roger", "goto forest");
        this.handleCommand("Roger", "cut down tree with axe");

    }

    private String handleCommand(String playerName, String command) {
        command = GameServer.joinStrings(playerName, ": ", command);
        return state.handleCommand(command);
    }

    @Test
    void testMoveArtefact() {
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a dark forest. You can see:
                          An old brass horn
                          Brass key
                          A heavy wooden log
                          A log cabin in the woods
                          """;
        assertEquals(expected, response);

        // move stick from storeroom
        response = this.handleCommand("Roger", "chip log");
        expected = "You chip the log and get a stick\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A stick
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // move stick to storeroom
        response = this.handleCommand("Roger", "move stick");
        expected = "Stick moved to storeroom\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testMoveFurniture() {
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a dark forest. You can see:
                          An old brass horn
                          Brass key
                          A heavy wooden log
                          A log cabin in the woods
                          """;
        assertEquals(expected, response);

        // move table from storeroom
        response = this.handleCommand("Roger", "build with log");
        expected = "You build a table with the log\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A table
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // move table to storeroom
        response = this.handleCommand("Roger", "move table");
        expected = "Table moved to storeroom\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testMoveCharacter() {
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a dark forest. You can see:
                          An old brass horn
                          Brass key
                          A heavy wooden log
                          A log cabin in the woods
                          """;
        assertEquals(expected, response);

        // move lumberjack from storeroom
        response = this.handleCommand("Roger", "blow horn");
        expected = "You blow the horn and as if by magic, a lumberjack appears !\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A burly wood cutter
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // move lumberjack to storeroom
        response = this.handleCommand("Roger", "move lumberjack");
        expected = "Lumberjack moved to storeroom\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testAlterPaths() {
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a dark forest. You can see:
                          An old brass horn
                          Brass key
                          A heavy wooden log
                          A log cabin in the woods
                          """;
        assertEquals(expected, response);

        // remove path from forest to cabin
        response = this.handleCommand("Roger", "remove cabin");
        expected = "Path to cabin removed\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   """;
        assertEquals(expected, response);

        // add path from forest to cabin
        response = this.handleCommand("Roger", "add cabin");
        expected = "Path to cabin added\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testInvalidCommands() {
        // Wrong location
        String response = this.handleCommand("Roger", "unlock trapdoor with key");
        String expected = "ERROR: This action cannot be performed in your location\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // unavailable entity
        this.handleCommand("Roger", "goto cabin");
        response = this.handleCommand("Roger", "unlock trapdoor with key");
        expected = "ERROR: This action requires an entity that is not in your inventory or location\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // no entity
        this.handleCommand("Roger", "goto forest");
        this.handleCommand("Roger", "get key");
        this.handleCommand("Roger", "goto cabin");
        response = this.handleCommand("Roger", "unlock");
        expected = "ERROR: No entity referenced in command\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // extraneous entity
        response = this.handleCommand("Roger", "unlock with axe");
        expected = "ERROR: An unrelated entity was referenced in command\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   """;
        assertEquals(expected, response);

        // produced artefact in another players inventory
        this.handleCommand("Roger", "goto forest");
        this.handleCommand("Roger", "get key");
        this.handleCommand("Roger", "goto cabin");
        this.handleCommand("Roger", "unlock with key");
        this.handleCommand("Roger", "goto cellar");
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dusty cellar. You can see:
                   Angry Elf
                   A magic wand
                   A log cabin in the woods
                   An empty room
                   """;
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "wave magic wand");
        expected = "You wave the magic wand and produce a gold coin\n";
        assertEquals(expected, response);
        this.handleCommand("Roger", "get coin");
        response = this.handleCommand("Roger", "wave magic wand");
        expected = "You wave the magic wand and produce a gold coin\n";
        assertEquals(expected, response);
        this.handleCommand("Roger", "get coin");
        // Roger now holds coin in inventory
        this.handleCommand("David", "goto cellar");
        response = this.handleCommand("David", "wave magic wand");
        expected = "ERROR: An entity produced by this command is in another players inventory\n";
        assertEquals(expected, response);
        // check coin is still in Roger's inventory
        response = this.handleCommand("Roger", "inv");
        expected = """
                   Your inventory contains:
                   A razor sharp axe
                   A gold coin
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testDuplicateTriggers() {
        this.handleCommand("Roger", "goto forest");
        String response = this.handleCommand("Roger", "look");
        String expected = """
                          You are in a dark forest. You can see:
                          An old brass horn
                          Brass key
                          A heavy wooden log
                          A log cabin in the woods
                          """;
        assertEquals(expected, response);

        // ambiguous action
        response = this.handleCommand("Roger", "break with axe");
        expected = "ERROR: Ambiguous command could be matched to multiple actions\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // ambiguous action
        response = this.handleCommand("Roger", "break");
        expected = "ERROR: Command could not be matched to an action\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // duplicate key phrase in command
        response = this.handleCommand("Roger", "break break horn");
        expected = "ERROR: Action trigger phrase was used more than once\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   An old brass horn
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // perform action on horn
        response = this.handleCommand("Roger", "break horn with axe");
        expected = "You break the horn with the axe\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   Brass key
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);

        // perform action on key
        response = this.handleCommand("Roger", "break key with axe");
        expected = "You break the key with the axe\n";
        assertEquals(expected, response);
        response = this.handleCommand("Roger", "look");
        expected = """
                   You are in a dark forest. You can see:
                   A heavy wooden log
                   A log cabin in the woods
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testTriggerContainsOtherTrigger() {
        String response = this.handleCommand("Roger", "firstword forest");
        String expected = "Action narration 1\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "firstword secondword forest");
        expected = "Action narration 2\n";
        assertEquals(expected, response);

        response = this.handleCommand("Roger", "firstword secondword thirdword forthword fifthword forest");
        expected = "Action narration 3\n";
        assertEquals(expected, response);
    }
}
