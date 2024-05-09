package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
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
            String entitiesFilePath = entitiesFile.getAbsolutePath();
            String actionsFilePath = actionsFile.getAbsolutePath();
            gameData.parseGameEntitiesFromFile(entitiesFilePath);
            gameData.parseActionsFromFile(actionsFilePath);
        } catch (Exception e) {
            throw new RuntimeException();
            //e.printStackTrace();
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
        // Give response
        // Basic commands
        String result = basicCommand(command);
        if(!result.equalsIgnoreCase("Not basic command")) {
            return result;
        }
        // Other actions command
        HashMap<String, HashSet<GameAction>> gameActions = gameData.getAllActions();
        for (HashMap.Entry<String, HashSet<GameAction>> entry : gameActions.entrySet()) {
            String trigger = entry.getKey();
            //HashSet<GameAction> actions = entry.getValue();
            if(command.contains(trigger)){
                String actionResult = checkMatch(trigger);
                return actionResult;
            }
        }
        // Health command
        if(command.contains("health")){
            return "Your current health level is: " + Integer.toString(gameData.getPlayer().getHealth());
        }
        return "Invalid command";
    }
    public String checkMatch(String trigger) {
        HashSet<GameAction> actionSet = gameData.getGameActions(trigger);
        String narration = "Action failed";
        // Check subjects
        for (GameAction action : actionSet) {
            if(action.getTriggerKeyphrases().contains(trigger)) {
                List<String> actionSubjects = new ArrayList<>();
                actionSubjects.addAll(action.getSubjectEntities());
                List<String> gameSubjects = new ArrayList<>();
                Locations currentLocation = gameData.getPlayer().getCurrentLocation();
                List<String> currentPath = gameData.getPaths(currentLocation.getName());
                gameSubjects.addAll(currentPath);
                List<String> currentArtefacts = new ArrayList<>();
                for (Map.Entry<String, Artefacts> entry : currentLocation.getAllArtefacts().entrySet()) {
                    currentArtefacts.add(entry.getKey());
                }
                gameSubjects.addAll(currentArtefacts);
                List<String> currentFurniture = new ArrayList<>();
                for (Map.Entry<String, Furniture> entry : currentLocation.getAllFurniture().entrySet()) {
                    currentFurniture.add(entry.getKey());
                }
                gameSubjects.addAll(currentFurniture);
                List<String> currentCharacters = new ArrayList<>();
                for (Map.Entry<String, Characters> entry : currentLocation.getAllCharacters().entrySet()) {
                    currentCharacters.add(entry.getKey());
                }
                gameSubjects.addAll(currentCharacters);
                Map<String, Artefacts> carryListArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
                List<String> currentCarryLists = new ArrayList<>();
                for (Map.Entry<String, Artefacts> entry : carryListArtefacts.entrySet()) {
                    currentCarryLists.add(entry.getKey());
                }
                gameSubjects.addAll(currentCarryLists);
                gameSubjects.add("health");
                if (gameSubjects.containsAll(actionSubjects)) {
                    // Check consumed
                    List<String> actionConsumeds = action.getConsumedEntities();
                    Map<String, List<String>> elementLists = new HashMap<>();
                    elementLists.put("locations", currentPath);
                    elementLists.put("artefacts", currentArtefacts);
                    elementLists.put("furniture", currentFurniture);
                    elementLists.put("characters", currentCharacters);
                    elementLists.put("carryLists", currentCarryLists);
                    List<String> healthList = new ArrayList<>();
                    healthList.add("health");
                    elementLists.put("health", healthList);
                    Map<String, List<String>> containsInLists = new HashMap<>();
                    for (String actionConsumed : actionConsumeds) {
                        List<String> containLists = new ArrayList<>();
                        for (Map.Entry<String, List<String>> entry : elementLists.entrySet()) {
                            if (entry.getValue().contains(actionConsumed)) {
                                containLists.add(entry.getKey());
                            }
                        }
                        containsInLists.put(actionConsumed, containLists);
                    }
                    for (Map.Entry<String, List<String>> entry : containsInLists.entrySet()) {
                        List<String> consumed = entry.getValue();
                        if (consumed.contains("locations")) {
                            for (String path : currentPath) {
                                if (actionConsumeds.contains(path)) {
                                    for (int i = 0; i < gameData.getAllPaths().size(); i++) {
                                        String[] removePath = gameData.getAllPaths().get(i);
                                        if (removePath[0].equals(currentLocation.getName()) && removePath[1].equals(path)) {
                                            gameData.getAllPaths().remove(i);
                                            i--;
                                        }
                                    }
                                }
                            }

                        }
                        if (consumed.contains("artefacts")) {
                            for (String consumedArtefact : currentArtefacts) {
                                if (actionConsumeds.contains(consumedArtefact)) {
                                    Artefacts artefact = currentLocation.getArtefacts(consumedArtefact);
                                    gameData.getLocation("storeroom").addArtefact(consumedArtefact, artefact.getDescription());
                                    currentLocation.getAllArtefacts().remove(consumedArtefact);
                                }
                            }
                        }
                        if (consumed.contains("furniture")) {
                            for (String consumedFurniture : currentFurniture) {
                                if (actionConsumeds.contains(consumedFurniture)) {
                                    Furniture furniture = currentLocation.getFurniture(consumedFurniture);
                                    gameData.getLocation("storeroom").addFurniture(consumedFurniture, furniture.getDescription());
                                    currentLocation.getAllFurniture().remove(consumedFurniture);
                                }
                            }
                        }
                        if (consumed.contains("characters")) {
                            for (String consumedCharacter : currentCharacters) {
                                if (actionConsumeds.contains(consumedCharacter)) {
                                    Characters character = currentLocation.getCharacters(consumedCharacter);
                                    gameData.getLocation("storeroom").addCharacter(consumedCharacter, character.getDescription());
                                    currentLocation.getAllCharacters().remove(consumedCharacter);
                                }
                            }
                        }
                        if (consumed.contains("carryLists")) {
                            for (String consumedCarrylist : currentCarryLists) {
                                if (actionConsumeds.contains(consumedCarrylist)) {
                                    Artefacts carryListArtefact = gameData.getPlayer().getCarryList().getArtefacts(consumedCarrylist);
                                    gameData.getLocation("storeroom").addArtefact(carryListArtefact.getName(), carryListArtefact.getDescription());
                                    gameData.getPlayer().getCarryList().getAllArtefacts().remove(consumedCarrylist);
                                }
                            }
                        }
                        //health
                        if (consumed.contains("health")) {
                            gameData.getPlayer().setHealth(false);
                            if(gameData.gameOver().contains("died")){
                                return gameData.gameOver();
                            }
                        }
                    }
                    if (gameSubjects.containsAll(actionConsumeds)) {
                        // Check produced
                        Locations storeroom = gameData.getLocation("storeroom");
                        List<String> storeroomArtefacts = new ArrayList<>();
                        for (Map.Entry<String, Artefacts> entry : storeroom.getAllArtefacts().entrySet()) {
                            storeroomArtefacts.add(entry.getKey());
                        }
                        gameSubjects.addAll(storeroomArtefacts);
                        List<String> storeroomFurniture = new ArrayList<>();
                        for (Map.Entry<String, Furniture> entry : storeroom.getAllFurniture().entrySet()) {
                            storeroomFurniture.add(entry.getKey());
                        }
                        gameSubjects.addAll(storeroomFurniture);
                        List<String> storeroomCharacters = new ArrayList<>();
                        for (Map.Entry<String, Characters> entry : storeroom.getAllCharacters().entrySet()) {
                            storeroomCharacters.add(entry.getKey());
                        }
                        gameSubjects.addAll(storeroomCharacters);
                        List<String> currentStoreroom = new ArrayList<>();
                        currentStoreroom.addAll(storeroomArtefacts);
                        currentStoreroom.addAll(storeroomFurniture);
                        currentStoreroom.addAll(storeroomCharacters);
                        elementLists.put("storeroom", currentStoreroom);
                        List<String> possiblePath = new ArrayList<>();
                        for (int i = 0; i < gameData.getAllPaths().size(); i++) {
                            String[] pathSet = gameData.getAllPaths().get(i);
                            possiblePath.add(pathSet[0]);
                            possiblePath.add(pathSet[1]);
                        }
                        elementLists.put("locations", possiblePath);
                        List<String> actionProduceds = action.getProducedEntities();
                        containsInLists.clear();
                        for (String actionProduce : actionProduceds) {
                            List<String> containLists = new ArrayList<>();
                            for (Map.Entry<String, List<String>> entry : elementLists.entrySet()) {
                                if (entry.getValue().contains(actionProduce)) {
                                    containLists.add(entry.getKey());
                                }
                            }
                            containsInLists.put(actionProduce, containLists);
                            if (actionProduceds.contains("health")) {
                                containLists.add("health");
                                containsInLists.put(actionProduce, containLists);
                            }
                            for (Map.Entry<String, List<String>> entry : containsInLists.entrySet()) {
                                List<String> produced = entry.getValue();
                                if (produced.contains("locations")) {
                                    List<String> allPath = new ArrayList<>();
                                    for (int i = 0; i < gameData.getAllPaths().size(); i++) {
                                        String[] pathSet = gameData.getAllPaths().get(i);
                                        allPath.add(pathSet[0]);
                                        allPath.add(pathSet[1]);
                                    }
                                    for (String path : allPath) {
                                        if (actionProduceds.contains(path)) {
                                            gameData.addPath(currentLocation.getName(), path);
                                        }
                                    }
                                }
                                if (produced.contains("storeroom")) {
                                    for (String producedArtefact : storeroomArtefacts) {
                                        if (actionProduceds.contains(producedArtefact)) {
                                            Artefacts artefact = storeroom.getArtefacts(producedArtefact);
                                            currentLocation.addArtefact(artefact.getName(), artefact.getDescription());
                                            storeroom.getAllArtefacts().remove(producedArtefact);
                                        }
                                    }
                                    for (String producedFurniture : storeroomFurniture) {
                                        if (actionProduceds.contains(producedFurniture)) {
                                            Furniture furniture = storeroom.getFurniture(producedFurniture);
                                            currentLocation.addFurniture(furniture.getName(), furniture.getDescription());
                                            storeroom.getAllFurniture().remove(producedFurniture);
                                        }
                                    }
                                    for (String producedCharacter : storeroomCharacters) {
                                        if (actionProduceds.contains(producedCharacter)) {
                                            Characters character = storeroom.getCharacters(producedCharacter);
                                            currentLocation.addCharacter(character.getName(), character.getDescription());
                                            storeroom.getAllCharacters().remove(producedCharacter);
                                        }
                                    }
                                }
                                //health
                                if (produced.contains("health")) {
                                    gameData.getPlayer().setHealth(true);
                                }
                            }
                        }
                        narration = action.getNarration();
                    } else {
                        return "Missing consumed entities";
                    }
                } else {
                    return "Missing subject entities";
                }
            }
        }
        return narration;
    }
    public int checkCommand(int n, String command, String name) {
        String[] commandArray = command.split("\\s+");
        List<String> commandList = Arrays.asList(commandArray);
        for (String commandClip : commandList) {
            if (commandClip.equalsIgnoreCase(name)) {
                n++;
                System.out.println(n);
            }
        }
        return n;
    }
    public String basicCommand(String command) {
        int n = 0;
        if(command.contains("inventory") || command.contains("inv")) {
            Map<String, Artefacts> carryListArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
            List<String> inventoryArtefacts = new ArrayList<>();
            for (Map.Entry<String, Artefacts> entry : carryListArtefacts.entrySet()) {
                //String artefactName = entry.getKey();
                Artefacts artefact = entry.getValue();
                inventoryArtefacts.add(artefact.getName());
            }
            String inventoryResult = String.join(System.lineSeparator(), inventoryArtefacts);
            return inventoryResult;
        }
        if(command.contains("get")) {
            Map<String, Artefacts> currentAllArtefacts = gameData.getPlayer().getCurrentLocation().getAllArtefacts();
            for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                String artefactName = entry.getKey();
                n = checkCommand(n, command, artefactName);
            }
            if (n <= 1) {
                for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                    String artefactName = entry.getKey();
                    Artefacts artefact = entry.getValue();
                    if(command.contains(artefactName)) {
                        gameData.getPlayer().getCarryList().addArtefact(artefactName, artefact.getDescription());
                        currentAllArtefacts.remove(artefactName);
                        return "You get " + artefactName + " from the " + gameData.getPlayer().getCurrentLocation().getName();
                    }
                }
                return "You cannot get this artefact";
            }
            return "Invalid command";
        }
        n = 0;
        if(command.contains("drop")){
            Map<String, Artefacts> storeroomArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
            for (Map.Entry<String, Artefacts> entry : storeroomArtefacts.entrySet()) {
                String artefactName = entry.getKey();
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
        n = 0;
        if(command.contains("goto")){
            Locations currentLocation = gameData.getPlayer().getCurrentLocation();
            List<String> currentPath = gameData.getPaths(currentLocation.getName());
            for (String path : currentPath) {
                n = checkCommand(n, command, path);
            }
            if (n <= 1) {
                for (String gotoPath : currentPath) {
                    n = checkCommand(n, command, gotoPath);
                    if (command.contains(gotoPath)) {
                        gameData.getPlayer().setCurrentLocation(gameData.getLocation(gotoPath));
                        return "You have gone to the " + gotoPath;
                    }
                }
                return "You cannot go to there";
            }
            return "Invalid command";
        }
        if(command.contains("look")){
            List<String> lookResult = new ArrayList<>();
            Locations currentLocation = gameData.getPlayer().getCurrentLocation();
            lookResult.add("You are in the " + currentLocation.getName() + ": " + currentLocation.getDescription());
            if(!currentLocation.getAllArtefacts().isEmpty()){
                lookResult.add("There are artefacts: ");
                Map<String, Artefacts> currentAllArtefacts = currentLocation.getAllArtefacts();
                List<String> currentArtefacts = new ArrayList<>();
                for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                    Artefacts artefact = entry.getValue();
                    currentArtefacts.add(artefact.getName() + ": " + artefact.getDescription());
                }
                lookResult.addAll(currentArtefacts);
            }
            if(!currentLocation.getAllFurniture().isEmpty()){
                lookResult.add("There are furniture: ");
                Map<String, Furniture> currentAllFurniture = currentLocation.getAllFurniture();
                List<String> currentFurniture = new ArrayList<>();
                for (Map.Entry<String, Furniture> entry : currentAllFurniture.entrySet()) {
                    Furniture furniture = entry.getValue();
                    currentFurniture.add(furniture.getName() + ": " + furniture.getDescription());
                }
                lookResult.addAll(currentFurniture);
            }
            if(!currentLocation.getAllCharacters().isEmpty()){
                lookResult.add("There are Characters: ");
                Map<String, Characters> currentAllCharacters = currentLocation.getAllCharacters();
                List<String> currentCharacters = new ArrayList<>();
                for (Map.Entry<String, Characters> entry : currentAllCharacters.entrySet()) {
                    Characters character = entry.getValue();
                    currentCharacters.add(character.getName() + ": " + character.getDescription());
                }
                lookResult.addAll(currentCharacters);
            }
            List<String> currentPath = gameData.getPaths(currentLocation.getName());
            lookResult.add("And you can goto: ");
            lookResult.addAll(currentPath);
            return String.join(System.lineSeparator(), lookResult);
        }
        return "Not basic command";
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
