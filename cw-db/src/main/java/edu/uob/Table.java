package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Table {
    private String currentDatabase;
    private String tableStoragePath;

    public void createTable(String tableName) {
        tableName = tableName.toLowerCase();
        currentDatabase = PathManager.getPathInstance().getDatabaseFolderPath();
        File table = new File(currentDatabase, tableName);
        this.tableStoragePath = table.getPath();
        System.out.println(this.currentDatabase);
       try {
            // Create the table file
           if (!table.exists()) {
               Files.createFile(Paths.get(currentDatabase, tableName + ".tab"));
               System.out.println("[OK]");
           }
        } catch(IOException ioe) {
            System.out.println("Can't seem to create a table: " + currentDatabase);
        }
    }

    //public void dropTable(String tableName) {

    //}
}
