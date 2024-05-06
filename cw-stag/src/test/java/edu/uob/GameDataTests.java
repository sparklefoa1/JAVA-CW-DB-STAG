package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class GameDataTests {
    private GameData parseLocation;

    @BeforeEach
    void setUp() {
        parseLocation = new GameData();
    }

    @Test
    void testParseLocations() throws IOException, ParseException {
        String filePath = "config" + File.separator + "basic-entities.dot";
        parseLocation.parseGameDataFromFile(filePath);
        System.out.println(parseLocation.getLocation("cabin").getDescription());
    }
}
