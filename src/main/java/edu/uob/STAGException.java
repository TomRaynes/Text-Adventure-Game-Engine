package edu.uob;

import edu.uob.entity.Container;
import edu.uob.entity.GameEntity;
import edu.uob.entity.Location;

import java.io.Serial;

public class STAGException extends Exception {

    @Serial private static final long serialVersionUID = 1;
    public STAGException(String message) {
        super(GameServer.joinStrings("ERROR: ", message, "\n"));
    }

    public static class IllegalMoveToInventoryException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public IllegalMoveToInventoryException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " cannot be added to inventory"));
        }
    }

    public static class EntityNotInInventoryException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityNotInInventoryException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " is not in your inventory"));
        }
    }

    public static class EntityInDifferentLocationException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityInDifferentLocationException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " is in another location"));
        }
    }

    public static class TryingToMoveContainerException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public TryingToMoveContainerException(Container container) {
            super(GameServer.joinStrings("The ", container.getName(), " cannot be moved"));
        }
    }

    public static class MalformedEntitiesFileException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public MalformedEntitiesFileException() {
            super("Entities file is malformed");
        }
    }

    public static class MalformedActionsFileException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public MalformedActionsFileException() {
            super("Actions file is malformed");
        }
    }

    public static class EntityInAnotherPlayersInventoryException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityInAnotherPlayersInventoryException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " is in another player's inventory"));
        }
    }

    public static class ProducedEntityInForeignInventoryException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public ProducedEntityInForeignInventoryException() {
            super("An entity produced by this command is in another players inventory");
        }
    }

    public static class NoActionFoundException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public NoActionFoundException() {
            super("Command could not be matched to an action");
        }
    }

    public static class AmbiguousCommandException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public AmbiguousCommandException() {
            super("Ambiguous command could be matched to multiple actions");
        }
    }

    public static class EntityInSubjectlessCommandException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityInSubjectlessCommandException(String command) {
            super(GameServer.joinStrings("An entity was referenced in ", command, " command"));
        }
    }

    public static class UnavailableEntityException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public UnavailableEntityException() {
            super("This action requires an entity that is not in your inventory or location");
        }
    }

    public static class NoSpecifiedEntityException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public NoSpecifiedEntityException(String command) {
            super(NoSpecifiedEntityException.getMessage(command));
        }

        private static String getMessage(String command) {

            if (command == null) {
                return "No entity referenced in command";
            }
            else return GameServer.joinStrings("No entity referenced in ", command, " command");
        }
    }

    public static class ExtraneousEntityException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public ExtraneousEntityException() {
            super("An unrelated entity was referenced in command");
        }
    }

    public static class MultipleEntitiesInBasicCommandException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public MultipleEntitiesInBasicCommandException(String command) {
            super(GameServer.joinStrings("Multiple entities were referenced in ",
                                             command, " command"));
        }
    }

    public static class NoPathToLocationException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public NoPathToLocationException(GameEntity fromLocation, GameEntity toLocation) {
            super(GameServer.joinStrings("There is no path from the ", fromLocation.getName(),
                                            " to the ", toLocation.getName()));
        }
    }

    public static class EntityAlreadyInInventoryException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityAlreadyInInventoryException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " is already in your inventory"));
        }
    }

    public static class PlayerAlreadyInLocationException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public PlayerAlreadyInLocationException(Location location) {
            super(GameServer.joinStrings("You are already in the ", location.getName()));
        }
    }

    public static class EntityNotLocationException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntityNotLocationException(GameEntity entity) {
            super(GameServer.joinStrings(entity.getTitleCaseName(), " is not a location"));
        }
    }

    public static class EntitiesFileParseException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public EntitiesFileParseException() {
            super("An error occurred while parsing entities file");
        }
    }

    public static class ActionInWrongLocationException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public ActionInWrongLocationException() {
            super("This action cannot be performed in your location");
        }
    }

    public static class ActionsFileParseException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public ActionsFileParseException() {
            super("An error occurred while parsing actions file");
        }
    }

    public static class IllegalPlayerNameException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public IllegalPlayerNameException(String illegalChars, boolean multipleChars) {
            super(IllegalPlayerNameException.getErrorMessage(illegalChars, multipleChars));
        }

        private static String getErrorMessage(String illegalChars, boolean multipleChars) {

            if (multipleChars) {
                return GameServer.joinStrings("The characters ", illegalChars,
                        " are not allowed in player names");
            }
            return GameServer.joinStrings("The character ", illegalChars,
                    " is not allowed in player names");
        }
    }

    public static class IncorrectTokenOrderException extends STAGException {

        @Serial private static final long serialVersionUID = 1;

        public IncorrectTokenOrderException() {
            super("Incorrect order of tokens in command");
        }
    }
}
