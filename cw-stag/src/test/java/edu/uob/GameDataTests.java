package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameDataTests {
    private GameData parseLocation;
    private List<String> subjects;

    @BeforeEach
    void setUp() {
        parseLocation = new GameData();
    }

    @Test
    void testParseLocations() throws IOException, ParseException, ParserConfigurationException, SAXException {
        String filePath = "config" + File.separator + "basic-entities.dot";
        parseLocation.parseGameEntitiesFromFile(filePath);
        String artefactName = "axe";
        Artefacts artefact = new Artefacts("axe", "A razor sharp axe");
        parseLocation.getPlayer().getCarryList().addArtefact(artefactName, String.valueOf(artefact));
        parseLocation.getPlayer().getCurrentLocation().getAllArtefacts().remove(artefactName);
        //System.out.println(parseLocation.getPlayer().getStoreroom().getAllArtefacts());
        //System.out.println(parseLocation.getPlayer().getCurrentLocation().getAllArtefacts());
        //System.out.println(parseLocation.getPlayer().getCurrentLocation().getAllCharacters());
        //System.out.println(parseLocation.getPlayer().getCurrentLocation().getName());
        //System.out.println(parseLocation.getLocation("cabin").getDescription());
        String actionFilePath = "config" + File.separator + "basic-actions.xml";
        parseLocation.parseActionsFromFile(actionFilePath);
        HashMap<String, HashSet<GameAction>> gameActions = new HashMap<>();
        gameActions = parseLocation.getAllActions();
        // 遍历 HashMap 的键值对
        for (HashMap.Entry<String, HashSet<GameAction>> entry : gameActions.entrySet()) {
            String trigger = entry.getKey();
            HashSet<GameAction> actions = entry.getValue();
            System.out.println("Trigger: " + trigger);
            System.out.println("Subject: " + actions.iterator().next().getSubjectEntities());
            System.out.println("Consumed: " + actions.iterator().next().getConsumedEntities());
            System.out.println("Produced: " + actions.iterator().next().getProducedEntities());
            System.out.println("Narration: " + actions.iterator().next().getNarration());
            System.out.println("---------");
        }

        //HashSet<GameAction> openAction = parseLocation.getGameActions("cut");
        /*for (GameAction gameAction : openAction) {
            // 获取当前 GameAction 对象的 subjects
            List<String> subjects = gameAction.getSubjectEntities();
            for (String subject : subjects) {
                System.out.println(subject);
            }
        }*/
    }
}
