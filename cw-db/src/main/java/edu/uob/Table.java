package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Table {
    private String tableStoragePath;

    public void createTable(String tableName) {
        tableStoragePath = PathManager.getPathInstance().getDatabaseFolderPath();
        System.out.println(tableStoragePath);
       try {
            // Create the table file
            Files.createFile(Paths.get(tableStoragePath, tableName + ".tab"));
            System.out.println("[OK]");
        } catch(IOException ioe) {
            System.out.println("Can't seem to create a table: " + tableStoragePath);
        }
    }
}
