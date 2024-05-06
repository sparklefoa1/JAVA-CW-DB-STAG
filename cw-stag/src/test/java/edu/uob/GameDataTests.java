package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class GameDataTests {
    private GameData location;

    @BeforeEach
    void setUp() {
        location = new GameData();
    }

    @Test
    void testParseLocations() throws IOException, ParseException {
        String filePath = "config" + File.separator + "basic-entities.dot";
        location.parseGameDataFromFile(filePath);
    }
}
