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
           if (!(currentDatabase == null)) {
               Files.createFile(Paths.get(this.currentDatabase, tableName + ".tab"));
               PathManager.getPathInstance().setTableStoragePath(table.getPath());
               System.out.println("[OK]");
           }
       } catch (FileAlreadyExistsException e) {
           System.out.println("Table already exists: " + this.currentDatabase);
       } catch(IOException ioe) {
           System.out.println("Can't seem to create a table: " + this.currentDatabase);
       }
    }

    public void dropTable(String tableName) {
        tableName = tableName.toLowerCase();
        currentDatabase = PathManager.getPathInstance().getDatabaseStoragePath();
        try {
            // Construct table storage path.
            File table = new File(currentDatabase, tableName + ".tab");//what is the difference of this.currentDatabase with currentDatabase
            System.out.println(currentDatabase);
            // Return if the table isn't exit.
            if (!table.exists()) {
                System.out.println("table is not exist: " + tableName + ".tab");
                return;
            }
            // Recursively delete table and its contents.
            dropTable(table);
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't seem to drop the table: " + currentDatabase + File.separator + tableName);
        }
    }

    private void dropTable(File table) throws IOException {
        if (!table.delete()) {
            throw new IOException("Can't seem to drop the database: " + table.getPath());
        }
    }
}
