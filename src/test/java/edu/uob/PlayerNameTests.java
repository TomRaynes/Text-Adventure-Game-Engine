package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerNameTests {

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

    }
}
