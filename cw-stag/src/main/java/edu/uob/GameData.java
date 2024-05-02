package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameData{
    private String entitiesFilePath;

    public GameData(File entitiesFile) {
        this.entitiesFilePath = entitiesFile.getPath();
    }
    public void parseLocations(){
        Parser parser = new Parser();
        try (FileReader reader = new FileReader(entitiesFilePath)) {
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();
            //need loop to read all locations
            int i = 0;
            while(sections.get(i).getSubgraphs() != null){
                ArrayList<Graph> locations[0] = sections.get(i).getSubgraphs();
                
                i++;
            }
            ArrayList<Graph> locations = sections.get(0).getSubgraphs();
            Graph firstLocation = locations.get(0);
            Node locationDetails = firstLocation.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            //String locationDescription = locationDetails.getAttribute("description");
        } catch (FileNotFoundException fnfd) {
            throw new RuntimeException("File not found: " + entitiesFilePath, fnfd);
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing entities file: " + entitiesFilePath, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    /*try{
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        // The locations will always be in the first subgraph
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        Graph firstLocation = locations.get(0);
        Node locationDetails = firstLocation.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();// insert to gameEntity+location class
        //remember the description
    } catch (FileNotFoundException fnfd) {
        System.err.println("Entities file can not be found:" + fnfd.getMessage());
    } catch (ParseException e) {
        System.err.println("Entities file can not be parsed:" + e.getMessage());
    }*/
}
