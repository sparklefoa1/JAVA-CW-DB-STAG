package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
        parseLocation.getPlayer().getStoreroom().addArtefact(artefactName, String.valueOf(artefact));
        parseLocation.getPlayer().getCurrentLocation().getAllArtefacts().remove(artefactName);
        System.out.println(parseLocation.getPlayer().getStoreroom().getAllArtefacts());
        System.out.println(parseLocation.getPlayer().getCurrentLocation().getAllArtefacts());
        //System.out.println(parseLocation.getPlayer().getCurrentLocation().getAllCharacters());
        //System.out.println(parseLocation.getPlayer().getCurrentLocation().getName());
        //System.out.println(parseLocation.getLocation("cabin").getDescription());
        String actionFilePath = "config" + File.separator + "basic-actions.xml";
        parseLocation.parseActionsFromFile(actionFilePath);
        HashSet<GameAction> openAction = parseLocation.getGameActions("cut");
        /*for (GameAction gameAction : openAction) {
            // 获取当前 GameAction 对象的 subjects
            List<String> subjects = gameAction.getSubjectEntities();
            for (String subject : subjects) {
                System.out.println(subject);
            }
        }*/
    }
}
