package edu.uob;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataBase {
    private String storageFolderPath;
    public String currentDatabase;

    public DataBase() {

    }

    public void createDatabase(String databaseName) {
        storageFolderPath = Paths.get("databases" + File.separator + databaseName).toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
            // Set "global database folder path"
            PathManager.getPathInstance().setDatabaseFolderPath("databases" + File.separator + databaseName);
            // Record current working database.
            currentDatabase = PathManager.getPathInstance().getDatabaseFolderPath();
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to create a database: " + storageFolderPath);
        }
    }

    public void dropDatabase() {
        if (storageFolderPath != null) {
            try {
                // Delete database folder
                Files.deleteIfExists(Paths.get(currentDatabase));
                System.out.println("[OK]");
            } catch (IOException ioe) {
                System.out.println("Failed to delete database folder: " + storageFolderPath);
            }
        } else {
            System.out.println("Database folder path is null.");
        }
    }
}
