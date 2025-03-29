package edu.uob;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public final class GameServer {

    GameState state;
    private static final String END_OF_TRANSMISSION = "\u0004";

    public static void main(String[] args) throws IOException {
        String entities = GameServer.joinStrings("config", File.separator, "extended-entities.dot");
        String actions = GameServer.joinStrings("config", File.separator, "extended-actions.xml");
        File entitiesFile = Paths.get(entities).toAbsolutePath().toFile();
        File actionsFile = Paths.get(actions).toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {

        try {
            state = new GameState(actionsFile, entitiesFile);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        return state.handleCommand(command);
    }

    public static String joinStrings(String... strings) {
        StringBuilder sb = new StringBuilder();

        for (String str : strings) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println(GameServer.joinStrings("Server listening on port ",
                                                       Integer.toString(portNumber)));
            while (!Thread.interrupted()) {
                try {
                    this.blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println(GameServer.joinStrings("Received message from ", incomingCommand));
                String result = this.handleCommand(incomingCommand);
                writer.write(result);
                writer.write(GameServer.joinStrings("\n", END_OF_TRANSMISSION, "\n"));
                writer.flush();
            }
        }
    }
}
