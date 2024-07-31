package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class GameData {
    private GamePlayer player;
    private Map<String, Locations> locations;
    private List<String[]> paths;
    private HashMap<String, HashSet<GameAction>> actions;

    public GameData() {
        player = new GamePlayer();
        locations = new HashMap<>();
        paths = new ArrayList<>();
        actions = new HashMap<>();
    }

    public String gameOver() {
        if (player.getHealth() == 0) {
            // Drop all items at the current location
            Map<String, Artefacts> carryListArtefacts = player.getCarryList().getAllArtefacts();
            for (Map.Entry<String, Artefacts> entry : carryListArtefacts.entrySet()) {
                Artefacts artefact = entry.getValue();
                player.getCurrentLocation().addArtefact(artefact.getName(), artefact.getDescription());
            }
            carryListArtefacts.clear(); // Clear player's carry list

            player.resetHealth();
            Locations initialLocation = player.getInitial();
            player.setCurrentLocation(initialLocation);

            return "You died and lost all of your items, you must return to the start of the game";
        }

        return "You are alive";
    }

    public void setInitialLocation(String initialLocationName) {
        Locations initialLocation = getLocation(initialLocationName);
        player.setInitialLocation(initialLocation);
        Locations initialCarryList = new Locations("carryList", "A player's carryList");
        player.setCarryList(initialCarryList);
    }

    public GamePlayer getPlayer() {
        return player;
    }

    // add location
    public void addLocation(String name, String description) {
        locations.put(name, new Locations(name, description));
    }

    // Add artefact
    public void addArtefact(String locationName, String artefactName, String artefactDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addArtefact(artefactName, artefactDescription);
        }
    }

    // Add furniture
    public void addFurniture(String locationName, String furnitureName, String furnitureDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addFurniture(furnitureName, furnitureDescription);
        }
    }

    // Add character
    public void addCharacter(String locationName, String characterName, String characterDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addCharacter(characterName, characterDescription);
        }
    }

    // Add path
    public void addPath(String from, String to) {
        paths.add(new String[]{from, to});
    }

    // Get locations
    public Locations getLocation(String name) {return locations.get(name);}

    // Get all locations
    public Map<String, Locations> getAllLocations() {
        return locations;
    }

    // Set path
    public void setPaths(List<String[]> paths) {this.paths = paths;}

    // Get path
    public List<String> getPaths(String fromName) {
        List<String> toLocations = new ArrayList<>();
        for (String[] path : paths) {
            if (path[0].equals(fromName)) {
                toLocations.add(path[1]);
            }
        }
        return toLocations;
    }

    // Get all path
    public List<String[]> getAllPaths() {
        return paths;
    }

    // Parse & store game entities data
    public void parseGameEntitiesFromFile(String filePath) throws IOException, ParseException {
        Parser parser = new Parser();
        try (FileReader reader = new FileReader(filePath); BufferedReader bufferedReader = new BufferedReader(reader)) {
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            parseLocations(sections.get(0));
            setInitialLocation(getFirstLocationName(sections.get(0)));
            parsePaths(sections.get(1));
        }
    }

    private void parseLocations(Graph locationsGraph) {
        ArrayList<Graph> locations = locationsGraph.getSubgraphs();
        for (Graph locationGraph : locations) {
            Node locationDetails = locationGraph.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");

            addLocation(locationName, locationDescription);
            parseSubGraphs(locationGraph, locationName);
        }
    }

    private void parseSubGraphs(Graph locationGraph, String locationName) {
        ArrayList<Graph> subGraphs = locationGraph.getSubgraphs();
        for (Graph subgraph : subGraphs) {
            if (subgraph.getId().getId().equals("artefacts")) {
                parseArtefacts(subgraph, locationName);
            } else if (subgraph.getId().getId().equals("furniture")) {
                parseFurniture(subgraph, locationName);
            } else if (subgraph.getId().getId().equals("characters")) {
                parseCharacters(subgraph, locationName);
            }
        }
    }

    private void parseArtefacts(Graph subgraph, String locationName) {
        ArrayList<Node> artefactsNodes = subgraph.getNodes(false);
        for (Node artefactNode : artefactsNodes) {
            String artefactName = artefactNode.getId().getId();
            String artefactDescription = artefactNode.getAttribute("description");
            addArtefact(locationName, artefactName, artefactDescription);
        }
    }

    private void parseFurniture(Graph subgraph, String locationName) {
        ArrayList<Node> furnitureNodes = subgraph.getNodes(false);
        for (Node furnitureNode : furnitureNodes) {
            String furnitureName = furnitureNode.getId().getId();
            String furnitureDescription = furnitureNode.getAttribute("description");
            addFurniture(locationName, furnitureName, furnitureDescription);
        }
    }

    private void parseCharacters(Graph subgraph, String locationName) {
        ArrayList<Node> charactersNodes = subgraph.getNodes(false);
        for (Node charactersNode : charactersNodes) {
            String charactersName = charactersNode.getId().getId();
            String charactersDescription = charactersNode.getAttribute("description");
            addCharacter(locationName, charactersName, charactersDescription);
        }
    }

    private String getFirstLocationName(Graph locationsGraph) {
        Node firstLocationDetails = locationsGraph.getSubgraphs().get(0).getNodes(false).get(0);
        return firstLocationDetails.getId().getId();
    }

    private void parsePaths(Graph pathsGraph) {
        ArrayList<Edge> paths = pathsGraph.getEdges();
        for (Edge path : paths) {
            Node fromLocation = path.getSource().getNode();
            String fromName = fromLocation.getId().getId();
            Node toLocation = path.getTarget().getNode();
            String toName = toLocation.getId().getId();
            addPath(fromName, toName);
        }
    }

    // Get actions
    public HashSet<GameAction> getGameActions(String triggerKeyphrase) {
        return actions.get(triggerKeyphrase);
    }

    public HashMap<String, HashSet<GameAction>> getAllActions() {
        return actions;
    }

    // Parse & store game actions data
    public void parseActionsFromFile(String filePath) throws ParserConfigurationException, SAXException, IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(filePath);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getChildNodes();
            for (int i = 0; i < actionNodes.getLength(); i++) {
                if (i % 2 != 0) {
                    Element actionElement = (Element) actionNodes.item(i);

                    List<String> triggerKeyphrases = parseElements(actionElement, "triggers");
                    List<String> subjectEntities = parseElements(actionElement, "subjects");
                    List<String> consumedEntities = parseElements(actionElement, "consumed");
                    List<String> producedEntities = parseElements(actionElement, "produced");

                    String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();

                    GameAction gameAction = new GameAction(triggerKeyphrases, subjectEntities, consumedEntities, producedEntities, narration);
                    storeGameAction(triggerKeyphrases, gameAction);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw e; // Re-throwing the caught exception
        }
    }

    private List<String> parseElements(Element parentElement, String tagName) {
        Element element = (Element) parentElement.getElementsByTagName(tagName).item(0);
        NodeList nodes = element.getChildNodes();
        List<String> elements = new ArrayList<>();
        for (int j = 0; j < nodes.getLength(); j++) {
            if (j % 2 != 0) {
                elements.add(nodes.item(j).getTextContent());
            }
        }
        return elements;
    }

    private void storeGameAction(List<String> triggerKeyphrases, GameAction gameAction) {
        for (String trigger : triggerKeyphrases) {
            if (!actions.containsKey(trigger)) {
                actions.put(trigger, new HashSet<>());
            }
            actions.get(trigger).add(gameAction);
        }
    }

}
