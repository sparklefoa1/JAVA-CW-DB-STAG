package edu.uob;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataBase {
    private String databaseStoragePath;
    //private String currentDatabase;

    public DataBase() {

    }

    public void createDatabase(String databaseName) {
        // Construct database storage path.
        File database = new File("databases", databaseName);
        this.databaseStoragePath = database.getAbsolutePath();
        try {
            // If database storage folder does not exist, then create it.
            if (!database.exists()) {
                database.mkdirs();
            }
            // Set "global" database folder path.
            PathManager.getPathInstance().setDatabaseFolderPath(database.getPath());
            // Record current working database.
            //currentDatabase = PathManager.getPathInstance().getDatabaseFolderPath();
            System.out.println("[OK]");
        } catch (SecurityException se) {
            System.out.println("Can't seem to create a database: " + this.databaseStoragePath);
        }

        /*this.databaseStoragePath = Paths.get("databases" + File.separator + databaseName).toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(this.databaseStoragePath));
            // Set "global database folder path"
            PathManager.getPathInstance().setDatabaseFolderPath("databases" + File.separator + databaseName);
            // Record current working database.
            currentDatabase = PathManager.getPathInstance().getDatabaseFolderPath();
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to create a database: " + this.databaseStoragePath);
        }*/
    }

    public void dropDatabase(String databaseName) {
        try {
            // Construct database storage path.
            File database = new File("databases", databaseName);
            this.databaseStoragePath = database.getAbsolutePath();

            // 如果文件夹不存在，则直接返回
            if (!database.exists()) {
                System.out.println("database is not exist: " + databaseName);
                return;
            }

            // 递归删除文件夹及其内容
            dropDatabase(database);
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to drop the database: " + this.databaseStoragePath);
        }
    }

    private void dropDatabase(File database) throws IOException {
        if (database.isDirectory()) {
            File[] files = database.listFiles();
            if (files != null) {
                for (File file : files) {
                    dropDatabase(file); // 递归删除子文件夹和文件
                }
            }
        }
        if (!database.delete()) {
            throw new IOException("Can't seem to drop the database: " + this.databaseStoragePath);
        }
    }
        /*if (databaseStoragePath != null) {
            try {
                // Delete database folder
                Files.deleteIfExists(Paths.get(currentDatabase));
                System.out.println("[OK]");
            } catch (IOException ioe) {
                System.out.println("Failed to delete database folder: " + databaseStoragePath);
            }
        } else {
            System.out.println("Database folder path is null.");
        }*/
}
