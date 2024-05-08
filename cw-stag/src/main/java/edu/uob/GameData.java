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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GameData {
    private Map<String, Locations> locations;
    private List<String[]> paths;
    private HashMap<String, HashSet<GameAction>> actions;
    public GameData() {
        locations = new HashMap<>();
        paths = new ArrayList<>();
        actions = new HashMap<>();
    }
    // 添加位置
    public void addLocation(String name, String description) {
        locations.put(name, new Locations(name, description));
    }
    // 添加道具
    public void addArtefact(String locationName, String artefactName, String artefactDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addArtefact(artefactName, artefactDescription);
        }
    }
    // 添加家具
    public void addFurniture(String locationName, String furnitureName, String furnitureDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addFurniture(furnitureName, furnitureDescription);
        }
    }
    // 添加角色
    public void addCharacter(String locationName, String characterName, String characterDescription) {
        Locations location = locations.get(locationName);
        if (location != null) {
            location.addCharacter(characterName, characterDescription);
        }
    }
    // 添加路径
    public void addPath(String from, String to) {
        paths.add(new String[]{from, to});
    }

    // 获取位置
    public Locations getLocation(String name) {
        return locations.get(name);
    }

    // 获取所有位置
    public Map<String, Locations> getAllLocations() {
        return locations;
    }

    // 获取路径
    public List<String[]> getPaths() {
        return paths;
    }

    // parse & store game data
    public void parseGameEntitiesFromFile(String filePath) throws FileNotFoundException, IOException, ParseException {
        Parser parser = new Parser();
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader(filePath);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

            // store locations & its contents
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            for (Graph locationGraph : locations) {
                Node locationDetails = locationGraph.getNodes(false).get(0);
                String locationName = locationDetails.getId().getId();
                String locationDescription = locationDetails.getAttribute("description");
                // store locations data
                addLocation(locationName, locationDescription);
                //System.out.println(locationName + " " + locationDescription);
                // store artefacts, furniture & characters data
                ArrayList<Graph> subGraphs = locationGraph.getSubgraphs();
                for (Graph subgraph : subGraphs) {
                    if (subgraph.getId().getId().equals("artefacts")) {
                        ArrayList<Node> artefactsNodes = subgraph.getNodes(false);
                        for (Node artefactsNode : artefactsNodes) {
                            String artefactsName = artefactsNode.getId().getId();
                            String artefactsDescription = artefactsNode.getAttribute("description");
                            // 将道具数据存储到GameData实例中，并与对应的位置关联
                            addArtefact(locationName, artefactsName, artefactsDescription);
                            //System.out.println(artefactsName + " " + artefactsDescription);
                        }
                    } else if (subgraph.getId().getId().equals("furniture")) {
                        ArrayList<Node> furnitureNodes = subgraph.getNodes(false);
                        for (Node furnitureNode : furnitureNodes) {
                            String furnitureName = furnitureNode.getId().getId();
                            String furnitureDescription = furnitureNode.getAttribute("description");
                            // 将家具数据存储到GameData实例中，并与对应的位置关联
                            addFurniture(locationName, furnitureName, furnitureDescription);
                            //System.out.println(furnitureName + " " + furnitureDescription);
                        }
                    } else if (subgraph.getId().getId().equals("characters")) {
                        ArrayList<Node> charactersNodes = subgraph.getNodes(false);
                        for (Node charactersNode : charactersNodes) {
                            String charactersName = charactersNode.getId().getId();
                            String charactersDescription = charactersNode.getAttribute("description");
                            // 将角色数据存储到GameData实例中，并与对应的位置关联
                            addCharacter(locationName, charactersName, charactersDescription);
                            //System.out.println(charactersName + " " + charactersDescription);
                        }
                    }
                }

            }
            // store paths
            ArrayList<Edge> paths = sections.get(1).getEdges();
            for (Edge path : paths) {
                Node fromLocation = path.getSource().getNode();
                String fromName = fromLocation.getId().getId();
                Node toLocation = path.getTarget().getNode();
                String toName = toLocation.getId().getId();
                // 将路径数据存储到GameData实例中
                addPath(fromName, toName);
                //System.out.println(fromName + " " + toName);
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /*public GameAction getGameAction(String triggerPhrase) {
        HashSet<GameAction> actionSet = actions.get(triggerPhrase);
        // 如果不存在这个 triggerPhrase 对应的动作集合，则返回 null
        if (actionSet == null || actionSet.isEmpty()) {
            return null;
        }
        return actionSet.iterator().next();//返回第一个动作对象
    }*/
    public HashSet<GameAction> getGameActions(String triggerKeyphrase) {
        return actions.get(triggerKeyphrase);
    }
    public void parseActionsFromFile(String filePath) throws ParserConfigurationException, SAXException, IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(filePath);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getChildNodes();
            for (int i = 0; i < actionNodes.getLength(); i++) {
                if(i % 2 != 0) {
                    Element actionElement = (Element) actionNodes.item(i);

                    Element triggers = (Element) actionElement.getElementsByTagName("triggers").item(0);
                    NodeList triggerNodes = triggers.getChildNodes();
                    List<String> triggerKeyphrases = new ArrayList<>();
                    for (int j = 0 ; j < triggerNodes.getLength(); j++){
                        if(j % 2 != 0) {
                            triggerKeyphrases.add(triggerNodes.item(j).getTextContent());
                        }
                    }
                    /*for (String phrase : triggerKeyphrases) {
                        System.out.println(phrase);
                    }*/
                    Element subjects = (Element) actionElement.getElementsByTagName("subjects").item(0);
                    NodeList subjectNodes = subjects.getChildNodes();
                    List<String> subjectEntities = new ArrayList<>();
                    for (int j = 0 ; j < subjectNodes.getLength(); j++){
                        if(j % 2 != 0) {
                            subjectEntities.add(subjectNodes.item(j).getTextContent());
                        }
                    }
                    /*for (String phrase : subjectEntities) {
                        System.out.println(phrase);
                    }*/
                    Element consumed = (Element) actionElement.getElementsByTagName("consumed").item(0);
                    NodeList consumedNodes = consumed.getChildNodes();
                    List<String> consumedEntities = new ArrayList<>();
                    for (int j = 0 ; j < consumedNodes.getLength(); j++){
                        if(j % 2 != 0) {
                            consumedEntities.add(consumedNodes.item(j).getTextContent());
                        }
                    }
                    /*for (String phrase : consumedEntities) {
                        System.out.println(phrase);
                    }*/
                    Element produced = (Element) actionElement.getElementsByTagName("produced").item(0);
                    NodeList producedNodes = produced.getChildNodes();
                    List<String> producedEntities = new ArrayList<>();
                    for (int j = 0 ; j < producedNodes.getLength(); j++){
                        if(j % 2 != 0) {
                            producedEntities.add(producedNodes.item(j).getTextContent());
                        }
                    }
                    /*for (String phrase : producedEntities) {
                        System.out.println(phrase);
                    }*/
                    String narration = actionElement.getElementsByTagName("narration").item(0).getTextContent();
                    //System.out.println(narration);

                    //store actions triggers
                    GameAction gameAction = new GameAction(triggerKeyphrases, subjectEntities, consumedEntities, producedEntities, narration);
                    // Add the action to actions map
                    for (String trigger : triggerKeyphrases) {
                        if (!actions.containsKey(trigger)) {
                            actions.put(trigger, new HashSet<>());
                        }
                        actions.get(trigger).add(gameAction);
                    }
                }

            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw e; // Re-throwing the caught exception
        }
    }
   /* // 设置玩家
    public void setPlayer(Player player) {
        this.player = player;
    }
    // 获取玩家
    public Player getPlayer() {
        return player;
    }*/
}
