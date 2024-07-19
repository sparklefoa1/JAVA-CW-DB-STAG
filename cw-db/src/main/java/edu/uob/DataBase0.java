package edu.uob;

import java.io.File;
import java.io.IOException;

public class DataBase0 {
    //private String databaseStoragePath;
    private String storagePath;

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getStoragePath() {
        return storagePath;
    }
    public void createDatabase(String databaseName) {
        databaseName = databaseName.toLowerCase();
        // Construct database storage path.
        setStoragePath("databases"+ File.separator + databaseName);
        File database = new File(getStoragePath());
        try {
            // Create the database storage folder if it doesn't already exist.
            if (!database.exists()) {
                database.mkdirs();
            }
            // Set "global" database.
            GlobalObject.getInstance().setDatabase(this);
        } catch (SecurityException se) {
            System.out.println("Can't seem to create a database: " + database.getPath());
        }
    }

    public void dropDatabase(String databaseName) {
        databaseName = databaseName.toLowerCase();
        try {
            // Construct database storage path.
            File database = new File(getStoragePath());
            // Return if the database isn't exit.
            if (!database.exists()) {
                System.out.println("database is not exist: " + databaseName);
                return;
            }
            // Recursively delete database and its contents.
            dropDatabase(database);
            //System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to drop the database: " + getStoragePath());
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