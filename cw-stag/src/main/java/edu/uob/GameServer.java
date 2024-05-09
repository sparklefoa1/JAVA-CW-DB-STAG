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
        // Basic commands //同时输入很多命令会怎样？按顺序执行。。。？ use else?
        String result = basicCommand(command);
        if(!result.equalsIgnoreCase("Not basic command")) {
            return result;
        }
        // Other actions command
        HashMap<String, HashSet<GameAction>> gameActions = gameData.getAllActions();
        for (HashMap.Entry<String, HashSet<GameAction>> entry : gameActions.entrySet()) {
            String trigger = entry.getKey();
            //HashSet<GameAction> actions = entry.getValue();
            //if(command.contains(trigger)){

            //}
        }
        return "";
    }
    public Boolean checkMatch(String trigger) {
        HashSet<GameAction> actionSet = gameData.getGameActions(trigger);
        List<String> items = new ArrayList<>();
        for (GameAction action : actionSet) {
            items.addAll(action.getSubjectEntities());
        }
        /*for (String item : items) {
            if(item.equalsIgnoreCase()){

            }
        }*/
        return false;
    }
    public String basicCommand(String command) {
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
        if(command.contains("get")){
            Map<String, Artefacts> currentAllArtefacts = gameData.getPlayer().getCurrentLocation().getAllArtefacts();
            for (Map.Entry<String, Artefacts> entry : currentAllArtefacts.entrySet()) {
                String artefactName = entry.getKey();
                Artefacts artefact = entry.getValue();
                if(command.contains(artefactName)){
                    gameData.getPlayer().getCarryList().addArtefact(artefactName, artefact.getDescription());
                    currentAllArtefacts.remove(artefactName);
                    return "You get " + artefactName + " from the " + gameData.getPlayer().getCurrentLocation().getName();
                }
            }
            return "You cannot get this artefact";
        }
        if(command.contains("drop")){
            Map<String, Artefacts> storeroomArtefacts = gameData.getPlayer().getCarryList().getAllArtefacts();
            for (Map.Entry<String, Artefacts> entry : storeroomArtefacts.entrySet()) {
                String artefactName = entry.getKey();
                Artefacts artefact = entry.getValue();
                if(command.contains(artefactName)){
                    gameData.getPlayer().getCurrentLocation().addArtefact(artefactName, artefact.getDescription());
                    storeroomArtefacts.remove(artefactName);
                    return "You drop " + artefactName + " to the " + gameData.getPlayer().getCurrentLocation().getName();
                }
            }
            return "You do not have this artefact";
        }
        if(command.contains("goto")){
            Locations currentLocation = gameData.getPlayer().getCurrentLocation();
            List<String> currentPath = gameData.getPaths(currentLocation.getName());
            for(String gotoPath : currentPath) {
                if (command.contains(gotoPath)) {
                    gameData.getPlayer().setCurrentLocation(gameData.getLocation(gotoPath));
                    return "You have gone to the " + gotoPath;
                }
            }
            return "You cannot go to there";
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
