package edu.uob;

import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameData {
    private Map<String, Locations> locations;
    private List<String[]> paths;
    public GameData() {
        locations = new HashMap<>();
        paths = new ArrayList<>();
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
    public void parseGameDataFromFile(String filePath) throws FileNotFoundException, IOException, ParseException {
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
   /* // 设置玩家
    public void setPlayer(Player player) {
        this.player = player;
    }
    // 获取玩家
    public Player getPlayer() {
        return player;
    }*/
}
