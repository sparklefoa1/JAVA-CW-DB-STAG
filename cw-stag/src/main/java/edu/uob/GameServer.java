package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    // Built-in commands
    public static final String CMD_INVENTORY = "inventory";
    public static final String CMD_INV = "inv";
    public static final String CMD_GET = "get";
    public static final String CMD_DROP = "drop";
    public static final String CMD_GOTO = "goto";
    public static final String CMD_LOOK = "look";

    private GameData gameData;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
        // TODO implement your server logic here
        // Initialize game & load game data
        gameData = new GameData();
        try {
            gameData.parseGameEntitiesFromFile(entitiesFile);
            gameData.parseActionsFromFile(actionsFile);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        command = command.toLowerCase();
        boolean actionFlag = false;
        boolean basicCommandFlag = false;

        // Give response
        // Health command
        if(command.contains("health")){
            return "Your current health level is: " + Integer.toString(gameData.getPlayer().getHealth());
        }

        // Check compound commands
        HashMap<String, HashSet<GameAction>> gameActions = gameData.getAllActions();
        for (HashMap.Entry<String, HashSet<GameAction>> entry : gameActions.entrySet()) {
            String trigger = entry.getKey();
            if (command.contains(trigger)) {
                actionFlag = true;
            }
        }
        if (!actionFlag) {
            // Handle basic commands
            String result = handleBasicCommand(command);
            if (!result.equalsIgnoreCase("Not basic command") && !result.equalsIgnoreCase("Compound Commands")) {
                if (gameData.getPlayer().getHealth() == 0) {
                    return gameData.gameOver();
                }
                return result;
            }
        }

        // Check compound commands
        if (command.contains(CMD_INV)
                || command.contains(CMD_INVENTORY)
                || command.contains(CMD_GET)
                || command.contains(CMD_DROP)
                || command.contains(CMD_GOTO)
                || command.contains(CMD_LOOK)) {
            basicCommandFlag = true;
        }
        if (!basicCommandFlag) {
            // Handle other actions command
            for (HashMap.Entry<String, HashSet<GameAction>> entry : gameActions.entrySet()) {
                String trigger = entry.getKey();

                // Calculate entities number
                int artefactNumber = 0;
                Map<String, Artefacts> currentAllArtefacts = gameData.getPlayer().getCurrentLocation().getAllArtefacts();
                for (String artefactName : currentAllArtefacts.keySet()) {
                    artefactNumber = checkCommand(artefactNumber, command, artefactName);
                }
                // and carry list
                int carryArtefactNumber = 0;
                Map<String, Artefacts> carryListArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
                for (String carryArtefactName : carryListArtefacts.keySet()) {
                    carryArtefactNumber = checkCommand(carryArtefactNumber, command, carryArtefactName);
                }
                int furnitureNumber = 0;
                Map<String, Furniture> currentAllFurniture = gameData.getPlayer().getCurrentLocation().getAllFurniture();
                for (String furnitureName : currentAllFurniture.keySet()) {
                    furnitureNumber = checkCommand(furnitureNumber, command, furnitureName);
                }
                int characterNumber = 0;
                Map<String, Characters> currentAllCharacters = gameData.getPlayer().getCurrentLocation().getAllCharacters();
                for (String charactersName : currentAllCharacters.keySet()) {
                    characterNumber = checkCommand(characterNumber, command, charactersName);
                }
                int pathNumber = 0;
                Locations currentLocation = gameData.getPlayer().getCurrentLocation();
                List<String> currentPath = gameData.getPaths(currentLocation.getName());
                for (String path : currentPath) {
                    pathNumber = checkCommand(pathNumber, command, path);
                }
                pathNumber = checkCommand(pathNumber, command, currentLocation.getName());
                int allThingsNumber = artefactNumber + carryArtefactNumber + furnitureNumber + characterNumber;
                // Handle other actions command
                if (command.contains(trigger)) {
                    if (artefactNumber < 2 && carryArtefactNumber < 2 && furnitureNumber < 2 && characterNumber < 2) {
                        if (pathNumber == 1 && allThingsNumber == 0) {
                            String actionResult = checkActionMatch(trigger);
                            if (gameData.getPlayer().getHealth() == 0) {
                                return gameData.gameOver();
                            }
                            return actionResult;
                        } else if (pathNumber == 0 && allThingsNumber > 0) {
                            String actionResult = checkActionMatch(trigger);
                            if (gameData.getPlayer().getHealth() == 0) {
                                return gameData.gameOver();
                            }
                            return actionResult;
                        } else {
                            return "Redundant Entities or Compound Commands or Missing entities";
                        }
                    }else {
                        return "Redundant Entities";
                    }
                }
            }
        }
        return "Invalid command";
    }

    // Check other actions command
    public String checkActionMatch(String trigger) {
        HashSet<GameAction> actionSet = gameData.getGameActions(trigger);
        String narration = "Action failed";

        for (GameAction action : actionSet) {
            if (action.getTriggerKeyphrases().contains(trigger)) {
                if (checkSubjects(action)) {
                    if (checkConsumed(action)) {
                        produceEntities(action);
                        narration = action.getNarration();
                    } else {
                        // Check if the health reached 0
                        if (gameData.getPlayer().getHealth() == 0) {
                            return gameData.gameOver();
                        }
                        return "Missing consumed entities";
                    }
                } else {
                    return "Missing subject entities";
                }
            }
        }
        return narration;
    }

    private boolean checkSubjects(GameAction action) {
        List<String> actionSubjects = new ArrayList<>(action.getSubjectEntities());
        List<String> gameSubjects = getCurrentGameSubjects();

        return gameSubjects.containsAll(actionSubjects);
    }

    private boolean checkConsumed(GameAction action) {
        List<String> actionConsumeds = action.getConsumedEntities();
        Map<String, List<String>> elementLists = getElementLists();
        Map<String, List<String>> containsInLists = getContainsInLists(actionConsumeds, elementLists);

        for (Map.Entry<String, List<String>> entry : containsInLists.entrySet()) {
            List<String> consumed = entry.getValue();
            String key = entry.getKey();
            if (consumed.contains("locations")) {
                removeLocationPaths(key);
            }
            if (consumed.contains("artefacts")) {
                removeArtefacts(key);
            }
            if (consumed.contains("furniture")) {
                removeFurniture(key);
            }
            if (consumed.contains("characters")) {
                removeCharacters(key);
            }
            if (consumed.contains("carryLists")) {
                removeCarryListArtefacts(key);
            }
            if (consumed.contains("health")) {
                gameData.getPlayer().changeHealth(false);
                if (gameData.getPlayer().getHealth() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void produceEntities(GameAction action) {
        List<String> actionProduced = action.getProducedEntities();
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        Locations storeroom = gameData.getLocation("storeroom");

        for (String entityName : actionProduced) {
            // Handle artefacts
            Artefacts artefact = storeroom.getArtefacts(entityName);
            if (artefact != null) {
                currentLocation.addArtefact(artefact.getName(), artefact.getDescription());
                storeroom.getAllArtefacts().remove(entityName);
                continue;
            }

            // Handle furniture
            Furniture furniture = storeroom.getFurniture(entityName);
            if (furniture != null) {
                currentLocation.addFurniture(furniture.getName(), furniture.getDescription());
                storeroom.getAllFurniture().remove(entityName);
                continue;
            }

            // Handle characters
            Characters character = storeroom.getCharacters(entityName);
            if (character != null) {
                currentLocation.addCharacter(character.getName(), character.getDescription());
                storeroom.getAllCharacters().remove(entityName);
            }

            // Handle location paths (if any)
            if (gameData.getLocation(entityName) != null) {
                addLocationPaths(entityName);
            }

            // Handle health recovery
            if ("health".equals(entityName)) {
                gameData.getPlayer().changeHealth(true);
            }
        }
    }

    private List<String> getCurrentGameSubjects() {
        List<String> gameSubjects = new ArrayList<>();
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        gameSubjects.addAll(gameData.getPaths(currentLocation.getName()));
        gameSubjects.addAll(currentLocation.getAllArtefacts().keySet());
        gameSubjects.addAll(currentLocation.getAllFurniture().keySet());
        gameSubjects.addAll(currentLocation.getAllCharacters().keySet());
        gameSubjects.addAll(gameData.getPlayer().getCarryList().getAllArtefacts().keySet());
        gameSubjects.add("health");
        return gameSubjects;
    }

    private Map<String, List<String>> getElementLists() {
        Map<String, List<String>> elementLists = new HashMap<>();
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        elementLists.put("locations", gameData.getPaths(currentLocation.getName()));
        elementLists.put("artefacts", new ArrayList<>(currentLocation.getAllArtefacts().keySet()));
        elementLists.put("furniture", new ArrayList<>(currentLocation.getAllFurniture().keySet()));
        elementLists.put("characters", new ArrayList<>(currentLocation.getAllCharacters().keySet()));
        elementLists.put("carryLists", new ArrayList<>(gameData.getPlayer().getCarryList().getAllArtefacts().keySet()));
        elementLists.put("health", Collections.singletonList("health"));
        return elementLists;
    }

    private Map<String, List<String>> getContainsInLists(List<String> entities, Map<String, List<String>> elementLists) {
        Map<String, List<String>> containsInLists = new HashMap<>();
        for (String entity : entities) {
            List<String> containLists = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : elementLists.entrySet()) {
                if (entry.getValue().contains(entity)) {
                    containLists.add(entry.getKey());
                }
            }
            containsInLists.put(entity, containLists);
        }
        return containsInLists;
    }

    private void removeLocationPaths(String path) {
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        List<String[]> allPaths = gameData.getAllPaths();
        for (int i = 0; i < allPaths.size(); i++) {
            String[] removePath = allPaths.get(i);
            if (removePath[0].equals(currentLocation.getName()) && removePath[1].equals(path)) {
                allPaths.remove(i);
                i--;
            }
        }
    }

    private void removeArtefacts(String artefactName) {
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        Artefacts artefact = currentLocation.getArtefacts(artefactName);
        gameData.getLocation("storeroom").addArtefact(artefactName, artefact.getDescription());
        currentLocation.getAllArtefacts().remove(artefactName);
    }

    private void removeFurniture(String furnitureName) {
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        Furniture furniture = currentLocation.getFurniture(furnitureName);
        gameData.getLocation("storeroom").addFurniture(furnitureName, furniture.getDescription());
        currentLocation.getAllFurniture().remove(furnitureName);
    }

    private void removeCharacters(String characterName) {
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        Characters character = currentLocation.getCharacters(characterName);
        gameData.getLocation("storeroom").addCharacter(characterName, character.getDescription());
        currentLocation.getAllCharacters().remove(characterName);
    }

    private void removeCarryListArtefacts(String artefactName) {
        Artefacts artefact = gameData.getPlayer().getCarryList().getArtefacts(artefactName);
        gameData.getLocation("storeroom").addArtefact(artefact.getName(), artefact.getDescription());
        gameData.getPlayer().getCarryList().getAllArtefacts().remove(artefactName);
    }

    private void addLocationPaths(String path) {
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        gameData.addPath(currentLocation.getName(), path);
    }

    // Check basic commands
    public int checkCommand(int n, String command, String name) {
        String[] commandArray = command.split("\\s+");
        List<String> commandList = Arrays.asList(commandArray);
        for (String commandClip : commandList) {
            if (commandClip.equalsIgnoreCase(name)) {
                n++;
                //System.out.println(n);
            }
        }
        return n;
    }

    public String handleBasicCommand(String command) {
        int n = 0;
        n = checkCommand(n, command, CMD_INVENTORY)
                + checkCommand(n, command, CMD_INV)
                + checkCommand(n, command, CMD_GET)
                + checkCommand(n, command, CMD_DROP)
                + checkCommand(n, command, CMD_GOTO)
                + checkCommand(n, command, CMD_LOOK);
        if (n == 1 ) {
            if (containsAny(command, CMD_INVENTORY, CMD_INV)) {
                return handleInventory();
            }
            if (command.contains(CMD_GET)) {
                return handleGet(command);
            }
            if (command.contains(CMD_DROP)) {
                return handleDrop(command);
            }
            if (command.contains(CMD_GOTO)) {
                return handleGoto(command);
            }
            if (command.contains(CMD_LOOK)) {
                return handleLook();
            }
            return "Not basic command";
        } else {
            return "Compound Commands";
        }
    }

    // Check whether it is an inventory built-in command
    private boolean containsAny(String command, String... keywords) {
        for (String keyword : keywords) {
            if (command.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String handleInventory() {
        // Get all items carried by the player
        Map<String, Artefacts> carryListArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
        List<String> inventoryArtefacts = new ArrayList<>();

        // Add all artefacts
        for (Artefacts artefact : carryListArtefacts.values()) {
            String artefactName = artefact.getName();
            inventoryArtefacts.add(artefactName);
        }
        String inventoryResult = String.join(System.lineSeparator(), inventoryArtefacts);

        return inventoryResult;
    }

    private String handleGet(String command) {
        int n = 0;
        Map<String, Artefacts> currentAllArtefacts = gameData.getPlayer().getCurrentLocation().getAllArtefacts();
        for (String artefactName : currentAllArtefacts.keySet()) {
            n = checkCommand(n, command, artefactName);
        }
        if (n <= 1) {
            for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                String artefactName = entry.getKey();
                Artefacts artefact = entry.getValue();
                if (command.contains(artefactName)) {
                    gameData.getPlayer().getCarryList().addArtefact(artefactName, artefact.getDescription());
                    currentAllArtefacts.remove(artefactName);
                    return "You get " + artefactName + " from the " + gameData.getPlayer().getCurrentLocation().getName();
                }
            }
            return "You cannot get this artefact";
        }
        return "Invalid command";
    }

    private String handleDrop(String command) {
        int n = 0;
        Map<String, Artefacts> storeroomArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
        for (String artefactName : storeroomArtefacts.keySet()) {
            n = checkCommand(n, command, artefactName);
        }
        if (n <= 1) {
            for (Map.Entry<String, Artefacts> entry : storeroomArtefacts.entrySet()) {
                String artefactName = entry.getKey();
                Artefacts artefact = entry.getValue();
                if (command.contains(artefactName)) {
                    gameData.getPlayer().getCurrentLocation().addArtefact(artefactName, artefact.getDescription());
                    storeroomArtefacts.remove(artefactName);
                    return "You drop " + artefactName + " to the " + gameData.getPlayer().getCurrentLocation().getName();
                }
            }
            return "You do not have this artefact";
        }
        return "Invalid command";
    }

    private String handleGoto(String command) {
        int n = 0;
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        List<String> currentPath = gameData.getPaths(currentLocation.getName());
        for (String path : currentPath) {
            n = checkCommand(n, command, path);
        }
        if (n <= 1) {
            for (String gotoPath : currentPath) {
                if (command.contains(gotoPath)) {
                    gameData.getPlayer().setCurrentLocation(gameData.getLocation(gotoPath));
                    return "You have gone to the " + gotoPath;
                }
            }
            return "You cannot go to there";
        }
        return "Invalid command";
    }

    private String handleLook() {
        List<String> lookResult = new ArrayList<>();
        Locations currentLocation = gameData.getPlayer().getCurrentLocation();
        lookResult.add("You are in the " + currentLocation.getName() + ": " + currentLocation.getDescription());

        addArtefactsDescription(currentLocation, lookResult);
        addFurnitureDescription(currentLocation, lookResult);
        addCharactersDescription(currentLocation, lookResult);

        List<String> currentPath = gameData.getPaths(currentLocation.getName());
        lookResult.add("And you can goto: ");
        lookResult.addAll(currentPath);

        return String.join(System.lineSeparator(), lookResult);
    }

    private void addArtefactsDescription(Locations location, List<String> description) {
        if (!location.getAllArtefacts().isEmpty()) {
            description.add("There are artefacts: ");
            Map<String, Artefacts> currentAllArtefacts = location.getAllArtefacts();

            List<String> currentArtefacts = new ArrayList<>();
            for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                Artefacts artefact = entry.getValue();
                String artefactDescription = artefact.getName() + ": " + artefact.getDescription();

                currentArtefacts.add(artefactDescription);
            }
            // Add item description into description list
            description.addAll(currentArtefacts);
        }
    }

    private void addFurnitureDescription(Locations location, List<String> description) {
        if (!location.getAllFurniture().isEmpty()) {
            description.add("There are furniture: ");
            Map<String, Furniture> currentAllFurniture = location.getAllFurniture();

            List<String> currentFurniture = new ArrayList<>();
            for (Map.Entry<String, Furniture> entry : currentAllFurniture.entrySet()) {
                Furniture furniture = entry.getValue();
                String furnitureDescription = furniture.getName() + ": " + furniture.getDescription();

                currentFurniture.add(furnitureDescription);
            }

            description.addAll(currentFurniture);
        }
    }

    private void addCharactersDescription(Locations location, List<String> description) {
        if (!location.getAllCharacters().isEmpty()) {
            description.add("There are Characters: ");
            Map<String, Characters> currentAllCharacters = location.getAllCharacters();

            List<String> currentCharacters = new ArrayList<>();
            for (Map.Entry<String, Characters> entry : currentAllCharacters.entrySet()) {
                Characters character = entry.getValue();
                String characterDescription = character.getName() + ": " + character.getDescription();

                currentCharacters.add(characterDescription);
            }

            description.addAll(currentCharacters);
        }
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
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
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
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
