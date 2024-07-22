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

    // Use method
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
                            row.addCell(columnNames[j], new Cell(columnNames[j], rowValues[j]));
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

    // Create method
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

    // Add method
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
            String attributeLowerCase = attribute.toLowerCase();
            if (attribute.equalsIgnoreCase("id")) {
                throw new IllegalArgumentException("[ERROR]: Manually updating the 'id' column is not allowed");
            }
            if (isReservedKeyword(attribute)) {
                throw new IllegalArgumentException("[ERROR]: Attribute name cannot be reserved words");
            }
            if (!columnNames.add(attributeLowerCase)) {
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

    // Drop method
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

    // Insert method
    public void insertIntoTable(String tableName, String[] values) throws IOException {
        if (currentDatabase == null) {
            throw new IllegalStateException("[ERROR]: No database is currently set up");
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new FileNotFoundException("[ERROR]: Table does not exist");
        }

        if (isReservedKeyword(tableName)) {
            throw new IllegalArgumentException("[ERROR]: Table name cannot be reserved words");
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
        row.addCell("id", new Cell("id", String.valueOf(newId)));
        for (int i = 0; i < values.length; i++) {
            String columnName = columns.get(i + 1).getName();
            String value = rowValues.get(i + 1);
            row.addCell(columnName, new Cell(columnName, value));
        }
        table.addRow(row);
    }

    // Select method
    public String selectColumnsFromTable(String tableName, String wildAttribList, String condition) throws IOException {
        if (currentDatabase == null) {
            throw new IllegalStateException("[ERROR]: No database is currently set up");
        }

        if (isReservedKeyword(tableName)) {
            throw new IllegalArgumentException("[ERROR]: Table name cannot be reserved words");
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            throw new FileNotFoundException("[ERROR]: Table does not exist");
        }

        StringBuilder result = new StringBuilder();
        List<Column> columns = table.getColumns();
        List<Row> rows = table.getRows();

        List<String> selectedColumns = new ArrayList<>();
        if ("*".equals(wildAttribList.trim())) {
            // Getting columns names
            for (Column column : columns) {
                selectedColumns.add(column.getName());
            }
        } else {
            // Getting attributes
            String[] attribArray = wildAttribList.split(",");
            for (String attrib : attribArray) {
                if (isReservedKeyword(attrib.trim())) {
                    throw new IllegalArgumentException("[ERROR]: Attribute name cannot be reserved words");
                } else {
                    selectedColumns.add(attrib.trim());
                }
            }
        }

        // Ready out columns names
        for (String columnName : selectedColumns) {
            result.append(columnName).append("\t");
        }
        result.setLength(result.length() - 1); // Removing last tab
        result.append("\n");

        boolean hasMatchingRow = false;

        // Ready out rows data
        for (Row row : rows) {
            if (condition == null || evaluateCondition(row, columns, condition)) {
                hasMatchingRow = true;
                Map<String, Cell> cellMap = new HashMap<>();

                for (Cell cell : row.getCells()) {
                    cellMap.put(cell.getColumnName(), cell);
                }

                for (String columnName : selectedColumns) {
                    Cell cell = cellMap.get(columnName);
                    if (cell != null) {
                        result.append(cell.getValue()).append("\t");
                    } else {
                        result.append("\t");
                    }
                }
                result.setLength(result.length() - 1); // Removing last tab
                result.append("\n");
            }
        }

        if (!hasMatchingRow) {
            // No matching rows, only return column names
            return result.toString();
        }

        return result.toString();
    }

    // Evaluate the condition on a row
    private boolean evaluateCondition(Row row, List<Column> columns, String condition) {
        String trimmedCondition = condition.trim();

        // Check ()
        if (trimmedCondition.startsWith("(") && trimmedCondition.endsWith(")")) {
            String innerCondition = trimmedCondition.substring(1, trimmedCondition.length() - 1).trim();
            return evaluateCondition(row, columns, innerCondition);
        }

        // Check AND or OR
        String[] boolOperators = {" AND ", " OR "};
        String upperCaseCondition = trimmedCondition.toUpperCase();
        for (String operator : boolOperators) {
            int operatorIndex = upperCaseCondition.indexOf(operator);
            if (operatorIndex != -1) {
                String leftCondition = trimmedCondition.substring(0, operatorIndex).trim();
                String rightCondition = trimmedCondition.substring(operatorIndex + operator.length()).trim();
                if (operator.trim().equalsIgnoreCase("AND")) {
                    return evaluateCondition(row, columns, leftCondition) && evaluateCondition(row, columns, rightCondition);
                } else if (operator.trim().equalsIgnoreCase("OR")) {
                    return evaluateCondition(row, columns, leftCondition) || evaluateCondition(row, columns, rightCondition);
                }
            }
        }

        // Check simple condition: attrName, comparator, value
        String[] parts = trimmedCondition.split("\\s+");
        if (parts.length == 3) {
            String attributeName = parts[0];
            String comparator = parts[1];
            String value = parts[2];

            if (isReservedKeyword(attributeName)) {
                throw new IllegalArgumentException("[ERROR]: Attribute name cannot be reserved words");
            }

            // Remove single quotes for string literals
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            // Convert boolean literals to uppercase
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                value = value.toUpperCase();
            }

            for (Column column : columns) {
                if (column.getName().equalsIgnoreCase(attributeName)) {
                    Cell cell = row.getCells().stream()
                            .filter(c -> c.getColumnName().equalsIgnoreCase(attributeName))
                            .findFirst()
                            .orElse(null);
                    if (cell == null) {
                        return false;
                    }
                    String cellValue = cell.getValue();
                    try {
                        switch (comparator.toUpperCase()) {
                            case "==":
                                return cellValue.equalsIgnoreCase(value);
                            case "!=":
                                return !cellValue.equalsIgnoreCase(value);
                            case "<":
                                return Double.parseDouble(cellValue) < Double.parseDouble(value);
                            case ">":
                                return Double.parseDouble(cellValue) > Double.parseDouble(value);
                            case "<=":
                                return Double.parseDouble(cellValue) <= Double.parseDouble(value);
                            case ">=":
                                return Double.parseDouble(cellValue) >= Double.parseDouble(value);
                            case "LIKE":
                                return cellValue.contains(value);
                            default:
                                throw new IllegalArgumentException("Invalid comparator: " + comparator);
                        }
                    } catch (NumberFormatException e) {
                        // If comparison fails due to type mismatch, return false
                        return false;
                    }
                }
            }
        }

        return false;
    }

    // Update method
    public String updateTable(String tableName, String nameValueList, String condition) throws IOException {
        if (currentDatabase == null) {
            return "[ERROR]: No database is currently set up";
        }

        if (isReservedKeyword(tableName)) {
           return "[ERROR]: Table name cannot be reserved words";
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            return "[ERROR]: Table does not exist";
        }

        // Parsing NameValueList
        Map<String, String> updates = parseNameValueList(nameValueList);
        if (updates == null) {
            return "[ERROR]: Invalid NameValueList syntax";
        }

        // Updating row in the table
        List<Row> rows = table.getRows();
        boolean updated = false;
        for (Row row : rows) {
            if (evaluateCondition(row, table.getColumns(), condition)) {
                for (Map.Entry<String, String> update : updates.entrySet()) {
                    if (update.getKey().equalsIgnoreCase("id")) {
                        return "[ERROR]: Cannot update ID column";
                    }

                    Cell cell = row.getCells().stream()
                            .filter(c -> c.getColumnName().equals(update.getKey()))
                            .findFirst()
                            .orElse(null);
                    if (cell != null) {
                        cell.setValue(update.getValue());
                        // Updating column type
                        Column column = table.getColumns().stream()
                                .filter(c -> c.getName().equals(update.getKey()))
                                .findFirst()
                                .orElse(null);
                        if (column != null) {
                            column.setDataType(inferDataType(update.getValue()));
                        }

                    } else {
                        return "[ERROR]: Column not found: " + update.getKey();
                    }
                }
                updated = true;
            }
        }

        if (updated) {
            saveTable(table, tableName);
            return "[OK]";
        } else {
            throw new IOException("No rows matched the condition");
        }
    }

    private Map<String, String> parseNameValueList(String nameValueList) throws IllegalArgumentException {
        Map<String, String> updates = new HashMap<>();
        String[] pairs = nameValueList.split(",");
        for (String pair : pairs) {
            String[] nameValue = pair.split("=");
            if (nameValue.length != 2) {
                return null;
            }
            String attributeName = nameValue[0].trim();
            String value = nameValue[1].trim();

            if (isReservedKeyword(attributeName)) {
                throw new IllegalArgumentException("[ERROR]: Attribute name cannot be reserved words");
            }

            // Remove single quotes for string literals
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            // Convert boolean literals to uppercase
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                value = value.toUpperCase();
            }

            // Cannot update ID column
            if (attributeName.equalsIgnoreCase("id")) {
                return null;
            }

            updates.put(attributeName, value);
        }
        return updates;
    }

    private String inferDataType(String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return "Boolean";
        } else if (value.equalsIgnoreCase("NULL")) {
            return "NULL";
        } else {
            try {
                Integer.parseInt(value);
                return "Integer";
            } catch (NumberFormatException e1) {
                try {
                    Float.parseFloat(value);
                    return "Float";
                } catch (NumberFormatException e2) {
                    return "String";
                }
            }
        }
    }

    // Alter method
    public String alterTable(String tableName, String alterationType, String attributeName) throws IOException {
        if (currentDatabase == null) {
            return "[ERROR]: No database is currently set up";
        }

        if (isReservedKeyword(tableName) || isReservedKeyword(attributeName)) {
            return "[ERROR]: Table or Attribute name cannot be reserved words";
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            return "[ERROR]: Table does not exist";
        }

        // Cannot alter ID column
        if (attributeName.equalsIgnoreCase("id")) {
            return "[ERROR]: Cannot alter ID column";
        }

        if (alterationType.equals("ADD")) {
            return addColumn(table, attributeName);
        } else if (alterationType.equals("DROP")) {
            return dropColumn(table, attributeName);
        } else {
            return "[ERROR]: Invalid alteration type: " + alterationType;
        }
    }

    private String addColumn(Table table, String columnName) throws IOException {
        // Checking column exist
        for (Column column : table.getColumns()) {
            if (column.getName().equals(columnName)) { // sensitive case
                return "[ERROR]: Column already exists: " + columnName;
            }
        }

        // Add new column (name
        table.addColumn(new Column(columnName, "String")); // Assuming data type is string

        // Update all rows, adding default values for new columns
        for (Row row : table.getRows()) {
            row.addCell(columnName, new Cell(columnName, "NULL"));
        }

        saveTable(table, table.getName());
        return "[OK]";
    }

    private String dropColumn(Table table, String columnName) throws IOException {
        // Checking column exist
        Column columnToRemove = null;
        for (Column column : table.getColumns()) {
            if (column.getName().equals(columnName)) { // sensitive case
                columnToRemove = column;
                break;
            }
        }

        if (columnToRemove == null) {
            return "[ERROR]: Column not found: " + columnName;
        }

        // Delete column
        table.getColumns().remove(columnToRemove);

        // Update all rows, deleting the data in this column
        for (Row row : table.getRows()) {
            row.getCells().remove(columnName);
        }

        saveTable(table, table.getName());
        return "[OK]";
    }

    // Delete method
    public String deleteFromTable(String tableName, String condition) throws IOException {
        if (currentDatabase == null) {
            return "[ERROR]: No database is currently set up";
        }

        if (isReservedKeyword(tableName)) {
            return "[ERROR]: Table name cannot be reserved words";
        }

        Table table = currentDatabase.getTable(tableName);
        if (table == null) {
            return "[ERROR]: Table does not exist";
        }

        // Deleting rows from the table
        List<Row> rows = table.getRows();
        boolean deleted = false;
        // Avoiding ConcurrentModificationException, use iterators to safely traverse and modify the collection
        Iterator<Row> iterator = rows.iterator();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (evaluateCondition(row, table.getColumns(), condition)) {
                iterator.remove();
                deleted = true;
            }
        }

        if (deleted) {
            saveTable(table, tableName);
            return "[OK]";
        } else {
            throw new IOException("No rows matched the condition");
        }
    }

    // Join method
    public String joinTables(String tableName1, String tableName2, String attribute1, String attribute2) {
        if (currentDatabase == null) {
            return "[ERROR]: No database is currently set up";
        }

        if (isReservedKeyword(tableName1)
                || isReservedKeyword(tableName2)
                || isReservedKeyword(attribute1)
                || isReservedKeyword(attribute2)) {
            return "[ERROR]: Table names and attributes cannot be reserved words";
        }

        Table table1 = currentDatabase.getTable(tableName1);
        Table table2 = currentDatabase.getTable(tableName2);

        if (table1 == null || table2 == null) {
            return "[ERROR]: One or both tables do not exist";
        }

        // Check if the join attributes are valid, if not 'id'
        if (!attribute1.equalsIgnoreCase("id") && table1.getColumn(attribute1) == null) {
            return "[ERROR]: Attribute " + attribute1 + " does not exist in " + tableName1;
        }
        if (!attribute2.equalsIgnoreCase("id") && table2.getColumn(attribute2) == null) {
            return "[ERROR]: Attribute " + attribute2 + " does not exist in " + tableName2;
        }

        StringBuilder result = new StringBuilder();
        result.append("id");

        // Add column names: OriginalTableName.AttributeName
        for (Column column : table1.getColumns()) {
            if (!column.getName().equalsIgnoreCase("id") && !column.getName().equalsIgnoreCase(attribute1)) {
                result.append("\t").append(tableName1).append(".").append(column.getName());
            }
        }
        for (Column column : table2.getColumns()) {
            if (!column.getName().equalsIgnoreCase("id") && !column.getName().equalsIgnoreCase(attribute2)) {
                result.append("\t").append(tableName2).append(".").append(column.getName());
            }
        }
        result.append("\n");

        List<Row> rows1 = table1.getRows();
        List<Row> rows2 = table2.getRows();

        int idCounter = 1;
        for (Row row1 : rows1) {
            String value1 = attribute1.equalsIgnoreCase("id") ? String.valueOf(row1.getId()) : row1.getCell(attribute1).getValue();
            for (Row row2 : rows2) {
                String value2 = attribute2.equalsIgnoreCase("id") ? String.valueOf(row2.getId()) : row2.getCell(attribute2).getValue();
                if (value1.equals(value2)) {
                    result.append(idCounter++);

                    // Add table1 column data
                    for (Column column : table1.getColumns()) {
                        if (!column.getName().equalsIgnoreCase("id") && !column.getName().equalsIgnoreCase(attribute1)) {
                            result.append("\t").append(row1.getCell(column.getName()).getValue());
                        }
                    }
                    // Add table2 column data
                    for (Column column : table2.getColumns()) {
                        if (!column.getName().equalsIgnoreCase("id") && !column.getName().equalsIgnoreCase(attribute2)) {
                            result.append("\t").append(row2.getCell(column.getName()).getValue());
                        }
                    }
                    result.append("\n");
                }
            }
        }

        return result.toString();
    }

    // Save the table to the corresponding file
    private void saveTable(Table table, String tableName) throws IOException {
        String tableFilePath = ROOT_DIRECTORY + File.separator + currentDatabase.getName() + File.separator + tableName + ".tab";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableFilePath))) {
            // Writing columns names
            List<Column> columns = table.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    writer.write("\t");
                }
                writer.write(columns.get(i).getName());
            }
            writer.newLine();

            // Writing rows data
            List<Row> rows = table.getRows();
            for (Row row : rows) {
                for (int i = 0; i < columns.size(); i++) {
                    if (i > 0) {
                        writer.write("\t");
                    }
                    writer.write(row.getCell(columns.get(i).getName()).getValue());
                }
                writer.newLine();
            }
        }
    }
}
