package edu.uob;

import java.io.*;
import java.util.*;

public class DatabaseManager {
    // Making sure that only one DatabaseManager instance manages the current database
    private static DatabaseManager instance;
    private Database currentDatabase;
    private Map<String, Database> databaseCache;
    private static final String ROOT_DIRECTORY = "databases";
    private static final Set<String> RESERVED_KEYWORDS = new HashSet<>(Arrays.asList(
            "USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN",
            "DATABASE", "TABLE", "INTO", "VALUES", "FROM", "WHERE", "SET",
            "AND", "ON", "ADD", "TRUE", "FALSE", "NULL", "OR", "LIKE"
    ));

    public static boolean isReservedKeyword(String name) {
        return RESERVED_KEYWORDS.contains(name.toUpperCase());
    }

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
        if (isReservedKeyword(databaseName)) {
            throw new IllegalArgumentException("[ERROR]: Database name cannot be reserved words");
        }
        String databasePath = ROOT_DIRECTORY + File.separator + databaseName;
        File databaseFile = new File(databasePath);
        if (!databaseFile.exists() || !databaseFile.isDirectory()) {
            throw new FileNotFoundException("[ERROR]: Database does not exist");
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
        }
        return tabFile;
    }

    public void createNewDatabase(String databaseName) throws IOException {
        if (isReservedKeyword(databaseName)) {
            throw new IllegalArgumentException("[ERROR]: Database name cannot be reserved words");
        }

        String databasePath = ROOT_DIRECTORY + File.separator + databaseName;
        File databaseFile = new File(databasePath);

        if (databaseFile.exists()) {
            throw new IllegalArgumentException("[ERROR]: Database already exists");
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
        if (currentDatabase == null) {
            throw new IllegalStateException("[ERROR]: No database is currently set up");
        }

        if (isReservedKeyword(tableName)) {
            throw new IllegalArgumentException("[ERROR]: Table name cannot be reserved words");
        }

        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        File tableFile = new File(tableFilePath);

        if (tableFile.exists()) {
            throw new IllegalArgumentException("[ERROR]: Table already exists");
        }

        if (tableFile.createNewFile()) {
            Table table = new Table(tableName);
            currentDatabase.addTable(table);
        } else {
            throw new IOException("Unable to create table file: " + tableName);
        }
    }

    public void addAttributesToTable(String tableName, String[] attributes) throws IOException {
        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        File tableFile = new File(tableFilePath);

        // Setting column names and add id column
        List<String> columns = new ArrayList<>();
        columns.add("id");
        // Avoiding duplicate column names
        Set<String> columnNames = new HashSet<>();
        columnNames.add("id");
        for (String attribute : attributes) {
            attribute= attribute.trim();
            if (attribute.equalsIgnoreCase("id")) {
                throw new IllegalArgumentException("[ERROR]: Manually updating the 'id' column is not allowed");
            }
            if (isReservedKeyword(attribute)) {
                throw new IllegalArgumentException("[ERROR]: Attribute cannot be reserved words");
            }
            if (!columnNames.add(attribute)) {
                throw new IllegalArgumentException("[ERROR]: duplicate column name " + attribute);
            }
            columns.add(attribute);
        }

        // Writing column names to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFile))) {
            writer.write(String.join("\t", columns));
            writer.newLine();
        }

        // Updating the table in memory
        Table table = currentDatabase.getTable(tableName);
        table.addColumn(new Column("id", "int"));
        for (String attribute : attributes) {
            table.addColumn(new Column(attribute.trim(), "String"));
        }
    }

    public void deleteDatabase(String databaseName) throws IOException {
        if (isReservedKeyword(databaseName)) {
            throw new IllegalArgumentException("[ERROR]: Database name cannot be reserved words");
        }

        String databasePath = ROOT_DIRECTORY + File.separator + databaseName;
        File databaseFile = new File(databasePath);

        if (!databaseFile.exists() || !databaseFile.isDirectory()) {
            throw new FileNotFoundException("[ERROR]: Database does not exist");
        }

        // Deleting the database directory and its contents
        deleteDirectory(databaseFile);
        // Removing a database from the cache
        databaseCache.remove(databaseName);
        // Setting the current database to null (if the current database is the deleted database
        if (currentDatabase != null && currentDatabase.getName().equals(databaseName)) {
            currentDatabase = null;
        }
    }

    private void deleteDirectory(File directory) throws IOException {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (!directory.delete()) {
            throw new IOException("Unable to delete database: " + directory.getAbsolutePath());
        }
    }

    public void deleteTable(String tableName) throws IOException {
        if (currentDatabase == null) {
            throw new IllegalStateException("[ERROR]: No database is currently set up");
        }

        if (isReservedKeyword(tableName)) {
            throw new IllegalArgumentException("[ERROR]: Table name cannot be reserved words");
        }

        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        File tableFile = new File(tableFilePath);

        if (!tableFile.exists()) {
            throw new FileNotFoundException("[ERROR]: Table does not exist");
        }

        if (!tableFile.delete()) {
            throw new IOException("Unable to delete table file: " + tableName);
        }
        currentDatabase.removeTable(tableName);
    }

    public void insertIntoTable(String tableName, String[] values) throws IOException {
        if (currentDatabase == null) {
            throw new IllegalStateException("[ERROR]: No database is currently set up");
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new FileNotFoundException("[ERROR]: Table does not exist");
        }

        // Getting the number of columns in the table
        int columnCount = table.getColumns().size();

        // Checking if the number of inserted values matches
        if (values.length != columnCount - 1) { // Except id column
            throw new IllegalArgumentException("[ERROR]: Too many or too few values inserted");
        }

        // Removing spaces between values
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }

        // Adding 1 to new id
        int newId = table.getRows().size() + 1;
        List<String> rowValues = new ArrayList<>();
        rowValues.add(String.valueOf(newId));

        // Converting the values and updating the data type of the column
        List<Column> columns = table.getColumns();
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            String dataType = "String"; // The default data type is String

            if (value.startsWith("'") && value.endsWith("'")) {
                // Removing ''
                value = value.substring(1, value.length() - 1);
                dataType = "String";
            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                value = value.toUpperCase();
                dataType = "Boolean";
            } else if (value.equalsIgnoreCase("NULL")) {
                value = "NULL";
                dataType = "NULL";
            } else {
                try {
                    if (value.contains(".")) {
                        Float.parseFloat(value);
                        dataType = "Float";
                    } else {
                        Integer.parseInt(value);
                        dataType = "Integer";
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Unable to convert value:" + value);
                }
            }
            rowValues.add(value);
            columns.get(i + 1).setDataType(dataType);
        }

        // Writing into the table file
        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFilePath, true))) {
            writer.write(String.join("\t", rowValues));
            writer.newLine();
        }

        // Updating table data in memory
        Row row = table.createNewRow();
        row.addCell("id", new Cell(String.valueOf(newId)));
        for (int i = 0; i < values.length; i++) {
            String columnName = columns.get(i + 1).getName();
            String value = rowValues.get(i + 1);
            row.addCell(columnName, new Cell(value));
        }
        table.addRow(row);
    }

}
