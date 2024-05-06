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
import java.util.Map;

public class GameData {
    /*private ArrayList<Location> locations;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furniture;
    private ArrayList<Character> characters;
    private Player player;*/

    public GameData() {
        /*locations = new ArrayList<>();
        artefacts = new ArrayList<>();
        furniture = new ArrayList<>();
        characters = new ArrayList<>();
        player = null;*/
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

            // 存储位置和路径数据
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            for (Graph locationGraph : locations) {
            //for (Graph section : sections) {
                //ArrayList<Node> nodes = section.getNodes(false);
                //if (!nodes.isEmpty()) {
                    //String sectionType = nodes.get(0).getId().getId();
                    //if (sectionType.startsWith("cluster")) {
                       // String locationName = sectionType.substring(7); // 移除 "cluster" 前缀
                Node locationDetails = locationGraph.getNodes(false).get(0);
                String locationName = locationDetails.getId().getId();
                String locationDescription = locationDetails.getAttribute("description");
                // 存储位置数据
                //addLocation(locationName, locationDescription);
                System.out.println(locationName + locationDescription);
                       /* // 存储家具数据
                        ArrayList<Graph> subgraphs = section.getSubgraphs();
                        for (Graph subgraph : subgraphs) {
                            if (subgraph.getId().getId().equals("furniture")) {
                                ArrayList<Node> furnitureNodes = subgraph.getNodes(false);
                                for (Node furnitureNode : furnitureNodes) {
                                    String furnitureName = furnitureNode.getId().getId();
                                    String furnitureDescription = furnitureNode.getAttribute("description");
                                    // 将家具数据存储到GameData实例中，并与对应的位置关联
                                    addFurniture(locationName, furnitureName, furnitureDescription);
                                }
                            } else if (subgraph.getId().getId().equals("artefacts")) {
                                ArrayList<Node> artefactNodes = subgraph.getNodes(false);
                                for (Node artefactNode : artefactNodes) {
                                    String artefactName = artefactNode.getId().getId();
                                    String artefactDescription = artefactNode.getAttribute("description");
                                    // 将道具数据存储到GameData实例中，并与对应的位置关联
                                    addArtefact(locationName, artefactName, artefactDescription);
                                }
                            }
                        }
                    } else if (sectionType.equals("paths")) {
                        // 存储路径数据
                        ArrayList<Edge> edges = section.getEdges();
                        for (Edge edge : edges) {
                            String fromLocation = edge.getSource().getNode().getId().getId();
                            String toLocation = edge.getTarget().getNode().getId().getId();
                            // 将路径数据存储到GameData实例中
                            addPath(fromLocation, toLocation);
                        }
                    }
                }*/
            }
            /*// store locations
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            for (Graph locationGraph : locations) {
                Node locationDetails = locationGraph.getNodes(false).get(0);
                String locationName = locationDetails.getId().getId();
                // 将位置数据存储到GameData实例中
                addLocation(locationName);
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
            }*/
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /*// add locations
    public void addLocation(String location) {
        locations.add(location);
    }

    // 添加物品
    public void addArtefact(Artefact artefact) {
        artefacts.add(artefact);
    }

    // 添加家具
    public void addFurniture(Furniture item) {
        furniture.add(item);
    }

    // 添加角色
    public void addCharacter(Character character) {
        characters.add(character);
    }

    // 设置玩家
    public void setPlayer(Player player) {
        this.player = player;
    }

    // 获取位置列表
    public ArrayList<Location> getLocations() {
        return locations;
    }

    // 获取物品列表
    public ArrayList<Artefact> getArtefacts() {
        return artefacts;
    }

    // 获取家具列表
    public ArrayList<Furniture> getFurniture() {
        return furniture;
    }

    // 获取角色列表
    public ArrayList<Character> getCharacters() {
        return characters;
    }

    // 获取玩家
    public Player getPlayer() {
        return player;
    }*/
}
