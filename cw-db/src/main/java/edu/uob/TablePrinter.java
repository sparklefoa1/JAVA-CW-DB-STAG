package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TablePrinter {

    // Print out whole table.
    public static void printOutTable(Table currentTable) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                String[] tokens = currentLine.split("\t");
                System.out.println(currentLine);
            }
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    // Print out special value/line.
    public static void printOUtLine(Table currentTable, String directColumnName, String indexValue) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            // Analyse the table header and find the index of the column.
            int columnIndex = getColumnIndex(lines.get(0), directColumnName);
            String headerLine = lines.get(0);
            System.out.println(headerLine);
            // Find the position of the indexValue.
            if (columnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens[columnIndex].equals(indexValue)) {
                        System.out.println(currentLine);
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    public static void printOutLineWithCharacter(Table currentTable, String directColumnName, String characterToFind) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            // Analyse the table header and find the index of the column.
            int columnIndex = getColumnIndex(lines.get(0), directColumnName);
            String headerLine = lines.get(0);
            System.out.println(headerLine);
            // Find the position of the indexValue.
            if (columnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens[columnIndex].contains(characterToFind)) {
                        System.out.println(currentLine);
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    public static void findColumnWithCharacter(Table currentTable, String directColumnName, String characterToFind) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            boolean specialValue = false;
            // Analyse the table header and find the index of the column.
            int columnIndex = getColumnIndex(lines.get(0), directColumnName);
            char columnHeader = lines.get(0).charAt(columnIndex);
            System.out.println(columnHeader);
            // Find the position of the indexValue.
            if (columnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens[columnIndex].contains(characterToFind)) {
                        specialValue = true;
                    }
                    // Print the value at the special column.
                    if(specialValue) {
                        System.out.println(tokens[columnIndex]);
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }
    // Find a column form the table.
    public static void findColumn(Table currentTable, String headerName, String indexValue) {
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            boolean specialValue = false;

            int columnIndex = getColumnIndex(lines.get(0), headerName);
            char columnHeader = lines.get(0).charAt(columnIndex);
            System.out.println(columnHeader);

            if (columnIndex != -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens[columnIndex].equals(indexValue)) {
                        specialValue = true;
                    }
                    // Print the value at the special column.
                    if(specialValue) {
                        System.out.println(tokens[columnIndex]);
                    }
                }
            } else {
                System.out.println("Column " + headerName + " not found.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public static void findColumn(Table currentTable, String headerName) {
        try {
            List<String> lines = readFile(currentTable.getStoragePath());

            int columnIndex = getColumnIndex(lines.get(0), headerName);
            char columnHeader = lines.get(0).charAt(columnIndex);
            System.out.println(columnHeader);

            if (columnIndex != -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Print the value at the column.
                    System.out.println(tokens[columnIndex]);
                }
            } else {
                System.out.println("Column " + headerName + " not found.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    // Find the special row needs to be printed.
    public static void findIndex(Table currentTable, String indirectColumnName, String indexValue, String directColumnName) {
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            boolean specialValue = false;
            // Analyse the table header and indirectly find the index of the column to be printed.
            int basedColumnIndex = getColumnIndex(lines.get(0), indirectColumnName); // Indirect index the value to be printed.
            int printColumnIndex = getColumnIndex(lines.get(0), directColumnName); // Direct index the value to be printed.
            int columnIndex = getColumnIndex(lines.get(0), directColumnName);
            char columnHeader = lines.get(0).charAt(columnIndex);
            System.out.println(columnHeader);
            // Find the position of the value needs to be printed.
            if (basedColumnIndex != -1 && printColumnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Return the print row index.
                    if (tokens[basedColumnIndex].equals(indexValue)) {
                        specialValue = true;
                    }
                    // Print the value at the special column.
                    if(specialValue) {
                        System.out.println(tokens[printColumnIndex]);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void findIndexWithCharacter(Table currentTable, String indirectColumnName, String characterToFind, String directColumnName) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            boolean specialValue = false;
            // Analyse the table header and find the index of the column.
            int basedColumnIndex = getColumnIndex(lines.get(0), indirectColumnName); // Indirect index the value to be printed.
            int printColumnIndex = getColumnIndex(lines.get(0), directColumnName); // Direct index the value to be printed.
            int columnIndex = getColumnIndex(lines.get(0), directColumnName);
            char columnHeader = lines.get(0).charAt(columnIndex);
            System.out.println(columnHeader);
            // Find the position of the indexValue.
            if (basedColumnIndex != -1 && printColumnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens[columnIndex].contains(characterToFind)) {
                        specialValue = true;
                    }
                    // Print the value at the special column.
                    if(specialValue) {
                        System.out.println(tokens[printColumnIndex]);
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    private static List<String> readFile(String currentTable) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(currentTable));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }
    private static int getColumnIndex(String headerLine, String columnName) {
        String[] header = headerLine.split("\t");
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
