package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameAndPunctuationTests {

    GameState state;

    @BeforeEach
    public void initialiseState() throws Exception {
        File actionsFile = Paths.get(GameServer.joinStrings("config", File.separator, "testCustom-actions.xml")).toAbsolutePath().toFile();
        File entitiesFile = Paths.get(GameServer.joinStrings("config", File.separator, "testCustom-entities.dot")).toAbsolutePath().toFile();
        state = new GameState(actionsFile, entitiesFile);
    }

    private String handleCommand(String playerName) {
        String command = GameServer.joinStrings(playerName, ": look");
        return state.handleCommand(command);
    }

    @Test
    void testLegalNames() {

        String response = this.handleCommand("Roger");
        String expected = """
                          You are in a log cabin in the woods. You can see:
                          A razor sharp axe
                          Some medicine
                          Magic potion
                          Wooden trapdoor
                          A dark forest
                          """;
        assertEquals(expected, response);
        response = this.handleCommand("David");
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
        response = this.handleCommand("Richard");
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
        response = this.handleCommand("Nick");
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

        // single character name
        response = this.handleCommand("x");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // space in name
        response = this.handleCommand("Roger Waters");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   x, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // apostrophe
        response = this.handleCommand("'");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   Roger Waters, a fellow adventurer
                   x, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // space
        response = this.handleCommand(" ");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                   ', a fellow adventurer
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   Roger Waters, a fellow adventurer
                   x, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // hyphen
        response = this.handleCommand("-");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                    , a fellow adventurer
                   ', a fellow adventurer
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   Roger Waters, a fellow adventurer
                   x, a fellow adventurer
                   """;
        assertEquals(expected, response);

        // long name
        response = this.handleCommand("QWERTYUIOPASDFGHJKLZXCVBNM' -qwertyuiopasdfghjklzxcvbnm");
        expected = """
                   You are in a log cabin in the woods. You can see:
                   A razor sharp axe
                   Some medicine
                   Magic potion
                   Wooden trapdoor
                   A dark forest
                    , a fellow adventurer
                   ', a fellow adventurer
                   -, a fellow adventurer
                   David, a fellow adventurer
                   Nick, a fellow adventurer
                   Richard, a fellow adventurer
                   Roger, a fellow adventurer
                   Roger Waters, a fellow adventurer
                   x, a fellow adventurer
                   """;
        assertEquals(expected, response);
    }

    @Test
    void testIllegalNames() {
        String response = this.handleCommand("Roger!");
        String expected = "ERROR: The character '!' is not allowed in player names\n";
        assertEquals(expected, response);

        response = this.handleCommand("David,?");
        expected = "ERROR: The characters ',' and '?' are not allowed in player names\n";
        assertEquals(expected, response);

        response = this.handleCommand("!@£$%^&*");
        expected = "ERROR: The characters '!', '$', '%', '&', '*', '@', '^' and '£' are not allowed in player names\n";
        assertEquals(expected, response);

        response = this.handleCommand("0123456789");
        expected = "ERROR: The characters '0', '1', '2', '3', '4', '5', '6', '7', '8' and '9' are not allowed in player names\n";
        assertEquals(expected, response);
    }

    @Test
    void testCommandNormalisation() {
        String response = state.handleCommand("Roger: look!");
        String expected = """
                          You are in a log cabin in the woods. You can see:
                          A razor sharp axe
                          Some medicine
                          Magic potion
                          Wooden trapdoor
                          A dark forest
                          """;
        assertEquals(expected, response);

        response = state.handleCommand("Roger:      look");
        assertEquals(expected, response);

        response = state.handleCommand("Roger: look      ");
        assertEquals(expected, response);

        response = state.handleCommand("Roger: l,.o:;o!?k");
        assertEquals(expected, response);

        response = state.handleCommand("Roger: lo  ok");
        expected = "ERROR: Command could not be matched to an action\n";
        assertEquals(expected, response);
    }
}
