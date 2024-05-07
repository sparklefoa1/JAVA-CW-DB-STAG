package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

public class GameAction {
    private String triggerKeyword;
    private List<String> subjects;
    private List<String> consumedEntities;
    private List<String> producedEntities;
    private String narration;
    public GameAction(String triggerKeyword, List<String> subjects, List<String> consumedEntities, List<String> producedEntities, String narration) {
        this.triggerKeyword = triggerKeyword;
        this.subjects = subjects;
        this.consumedEntities = consumedEntities;
        this.producedEntities = producedEntities;
        this.narration = narration;
    }
}
