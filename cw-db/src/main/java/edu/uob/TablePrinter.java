package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
