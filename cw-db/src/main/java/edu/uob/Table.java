package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;

public class Table {
    private String currentDatabase;
    private String tableStoragePath;

    public Table() {}

    public void createTable(String tableName) {
        tableName = tableName.toLowerCase();
        this.currentDatabase = PathManager.getPathInstance().getDatabaseStoragePath();
        File table = new File(this.currentDatabase, tableName);
        //this.tableStoragePath = table.getPath();
        System.out.println(this.currentDatabase);
       try {
            // Create the table file
           //if (!table.exists()) {
               Files.createFile(Paths.get(this.currentDatabase, tableName + ".tab"));
               PathManager.getPathInstance().setTableStoragePath(table.getPath());
               System.out.println("[OK]");
           //}
       } catch (FileAlreadyExistsException e) {
           //
           System.out.println("Table already exists: " + this.currentDatabase);
       } catch(IOException ioe) {
           System.out.println("Can't seem to create a table: " + this.currentDatabase);
       }
    }

    //public void dropTable(String tableName) {

    //}
}
