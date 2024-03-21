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
    public static void  printOutTable(Table currentTable) {
        String tablePath = currentTable.getStoragePath();
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                String[] tokens = currentLine.split("\t");
                System.out.println(currentLine);
            }
            System.out.println("[OK]");//位置移到前面去
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    // Print out special value/line.
    public static void  printOUtLine(Table currentTable, String directColumnName, String indexValue) {
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
            System.out.println("[OK]");//位置移到前面去
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
            System.out.println("[OK]");//位置移到前面去
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }

    // Find a column form the table.
    public static void findColumn(Table currentTable, String headerName, boolean specialValue) {
        try {
            List<String> lines = readFile(currentTable.getStoragePath());

            int columnIndex = getColumnIndex(lines.get(0), headerName);

            if (columnIndex != -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Print the value at the special column.
                    if(specialValue) {
                        System.out.println(tokens[columnIndex]);
                    } else {
                        // Print the special value at the special column.
                    }
                }
                System.out.println("[OK]");
            } else {
                System.out.println("Column " + headerName + " not found.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    // Find the special row needs to be printed.
    public static int findIndex(Table currentTable, String indirectColumnName, String indexValue, String directColumnName) {//在编译器里写一个方法，-1时为false，其他时候为true来筛选要打印的值
        try {
            List<String> lines = readFile(currentTable.getStoragePath());
            // Analyse the table header and indirectly find the index of the column to be printed.
            int basedColumnIndex = getColumnIndex(lines.get(0), indirectColumnName); // Indirect index the value to be printed.
            int printColumnIndex = getColumnIndex(lines.get(0), directColumnName); // Direct index the value to be printed.
            // Find the position of the value needs to be printed.
            if (basedColumnIndex != -1 && printColumnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Return the print row index.
                    if (tokens[basedColumnIndex].equals(indexValue)) {
                        return i;
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;
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
