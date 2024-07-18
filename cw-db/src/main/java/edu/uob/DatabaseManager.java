package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    // Making sure that only one DatabaseManager instance manages the current database
    private static DatabaseManager instance;
    private Database currentDatabase;
    private Map<String, Database> databaseCache;
    private static final String ROOT_DIRECTORY = "databases";

    private DatabaseManager() {
        databaseCache = new HashMap<>();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void setCurrentDatabase(String databaseName) throws IOException {
        String databasePath = ROOT_DIRECTORY + File.separator + databaseName;
        File databaseFile = new File(databasePath);
        if (!databaseFile.exists() || !databaseFile.isDirectory()) {
            throw new FileNotFoundException("The database path does not exist");
        }

        if (databaseCache.containsKey(databaseName)) {
            currentDatabase = databaseCache.get(databaseName);
        } else {
            // Loading a database from the directory
            Database aimDatabase = loadDatabaseFromPath(databaseFile, databaseName);
            databaseCache.put(databaseName, aimDatabase);
            currentDatabase = aimDatabase;
        }
    }

    public Database getCurrentDatabase() {
        return currentDatabase;
    }

    private Database loadDatabaseFromPath(File databaseFile, String databaseName) throws IOException {
        Database aimDatabase = new Database(databaseName);
        // Getting all tables from the database directory
        File[] tableFiles = databaseFile.listFiles((dir, name) -> name.endsWith(".tab"));
        if (tableFiles != null) {
            for (File tableFile : tableFiles) {
                String tableName = tableFile.getName().replace(".tab", "");
                Table table = new Table(tableName);
                List<String[]> tableData = readDataFromFile(tableFile.getAbsolutePath());

                if (!tableData.isEmpty()) {
                    // 1st line is column names
                    String[] columnNames = tableData.get(0);
                    for (String columnName : columnNames) {
                        table.addColumn(new Column(columnName, "String"));
                    }

                    // Data rows begin from 2nd row
                    for (int i = 1; i < tableData.size(); i++) {
                        String[] rowValues = tableData.get(i);
                        Row row = table.createNewRow();
                        for (int j = 0; j < rowValues.length; j++) {
                            row.addCell(columnNames[j], new Cell(rowValues[j]));
                        }
                        table.addRow(row);
                    }
                }
                aimDatabase.addTable(table);
            }
        }
        return aimDatabase;
    }

    public List<String[]> readDataFromFile(String filePath) throws IOException {
        List<String[]> tabFile = new ArrayList<>();
        File fileToRead = new File(filePath);

        // Trying to read data of file
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead))) {
            String fileLine;
            // Reading data of file
            while ((fileLine = bufferedReader.readLine()) != null) {
                // Getting data to string
                String[] values = fileLine.split("\t");
                tabFile.add(values);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Table not found: " + filePath);
            throw e;
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
            throw e;
        }
        return tabFile;
    }

    public void createNewDatabase(String databaseName) throws IOException {
        String databasePath = ROOT_DIRECTORY + File.separator + databaseName;
        File databaseFile = new File(databasePath);

        if (databaseFile.exists()) {
            throw new IOException("[ERROR]: Database already exists");
        }

        if (databaseFile.mkdir()) {
            Database aimDatabase = new Database(databaseName);
            databaseCache.put(databaseName, aimDatabase);
            currentDatabase = aimDatabase;
        } else {
            throw new IOException("Unable to create database directory: " + databaseName);
        }
    }

    public void createNewTable(String tableName) throws IOException {
        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        File tableFile = new File(tableFilePath);

        if (tableFile.exists()) {
            throw new IOException("[ERROR]: Table already exists");
        }

        if (tableFile.createNewFile()) {
            Table table = new Table(tableName);
            currentDatabase.addTable(table);
        } else {
            throw new IOException("Unable to create table file: " + tableName);
        }
    }
}
