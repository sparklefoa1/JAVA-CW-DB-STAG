package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Table {
    private String storageFolderPath;
    public Table(String tableName) {
        storageFolderPath = Paths.get("databases" + File.separator + tableName).toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
            System.out.println("[OK]");
        } catch(IOException ioe) {
            System.out.println("Can't seem to create a database: " + storageFolderPath);
        }
    }
}
