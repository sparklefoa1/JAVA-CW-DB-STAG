package edu.uob;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataBase {
    private String storageFolderPath;
    public DataBase(String databaseName) {
        storageFolderPath = Paths.get("databases" + File.separator + databaseName).toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to create a database: " + storageFolderPath);
        }
    }

    public void dropDatabase() {
        try {
            // Delete database folder
            Files.deleteIfExists(Paths.get(storageFolderPath));
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Failed to delete database folder: " + storageFolderPath);
        }
    }
}
