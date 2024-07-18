package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FMTests {
    private FileManager fileManager;
    private String filePath;

    @BeforeEach
    public void setup() {
        fileManager = new FileManager();
    }

    @Test
    public void testReadDateFromFile() {
        filePath = "databases" + File.separator + "people.tab";
        try {
            List<String[]> tabFile = fileManager.readDataFromFile(filePath);
            assertNotNull(tabFile, "Records should not be null");
            assertFalse(tabFile.isEmpty(), "Records should not be empty");
        }  catch (FileNotFoundException e) {
            // Dealing with file not found errors
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            // Dealing with other IO errors
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    @Test
    public void testReadDateFromFileNotFound() {
        filePath = "databases" + File.separator + "p.tab";
        assertThrows(IOException.class, () -> {
            fileManager.readDataFromFile(filePath);
        }, "Expected to throw FileNotFoundException due to no file in the databases");
    }

    // Testing saving data in data structures
    @Test
    public void testSaveData() {
        filePath = "databases" + File.separator + "people.tab";
        try {
            Database db = fileManager.populateDatabase(filePath);

            // Modifying ages
            Table table = db.getTable("SampleTable");
            for (Row row : table.getRows()) {
                Cell ageCell = row.getCell("Age");
                if (ageCell != null) {
                    int age = Integer.parseInt(ageCell.getValue());
                    ageCell.setValue(String.valueOf(age + 1));
                }
            }

            // Resaving data to file
            fileManager.saveDatabaseToFile(db, filePath);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

}
