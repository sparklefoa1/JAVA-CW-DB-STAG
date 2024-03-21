package edu.uob;

import java.io.*;
import java.io.IOException;

public class Dpath {
    //private String databaseStoragePath;

    public Dpath() {}

    public void createDatabase(String databaseName) {
        databaseName = databaseName.toLowerCase();
        // Construct database storage path.
        File database = new File("databases", databaseName);
        try {
            // Create the database storage folder if it doesn't already exist.
            if (!database.exists()) {
                database.mkdirs();
                System.out.println("[OK]");
            }
            // Set "global" database folder path.
            PathManager.getPathInstance().setDatabaseStoragePath(database.getPath());
        } catch (SecurityException se) {
            System.out.println("Can't seem to create a database: " + database.getPath());
        }
    }

    public void dropDatabase(String databaseName) {
        databaseName = databaseName.toLowerCase();
        try {
            // Construct database storage path.
            File database = new File("databases", databaseName);
            // Return if the database isn't exit.
            if (!database.exists()) {
                System.out.println("database is not exist: " + databaseName);
                return;
            }
            // Recursively delete database and its contents.
            dropDatabase(database);
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to drop the database: " + "databases" + File.separator + databaseName);
        }
    }

    private void dropDatabase(File database) throws IOException {
        if (database.isDirectory()) {
            File[] tables = database.listFiles();
            if (tables != null) {
                // Loop to delete tables in database.
                for (File table : tables) {
                    dropDatabase(table);
                }
            }
        }
        if (!database.delete()) {
            throw new IOException("Can't seem to drop the database: " + database.getPath());
        }
    }
}
