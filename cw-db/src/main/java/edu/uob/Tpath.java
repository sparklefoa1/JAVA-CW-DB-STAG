package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;

public class Tpath {
    private String currentDatabase;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void createTable(String tableName) {
        tableName = tableName.toLowerCase();
        currentDatabase = PathManager.getPathInstance().getDatabaseStoragePath();
        File table = new File(currentDatabase, tableName);
       try {
            // Create the table file
           if (!(currentDatabase == null)) {
               Files.createFile(Paths.get(currentDatabase, tableName + ".tab"));
               PathManager.getPathInstance().setTableStoragePath(table.getPath());
               System.out.println("[OK]");
           }
       } catch (FileAlreadyExistsException e) {
           System.out.println("Table already exists: " + currentDatabase);
       } catch(IOException ioe) {
           System.out.println("Can't seem to create a table: " + currentDatabase);
       }
    }

    public void dropTable(String tableName) {
        tableName = tableName.toLowerCase();
        currentDatabase = PathManager.getPathInstance().getDatabaseStoragePath();
        try {
            File table = new File(currentDatabase, tableName + ".tab");//what is the difference of this.currentDatabase with currentDatabase
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

    public void printOutFile() {
        String filePath ="databases" + File.separator + "people.tab";
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferReader = new BufferedReader(reader);
            String tableFile;
            while ((tableFile = bufferReader.readLine()) != null) {
                System.out.println(tableFile);
            }
            bufferReader.close();
            reader.close();
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + filePath);
        }
    }
}
