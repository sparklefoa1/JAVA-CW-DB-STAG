package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableModification {

    // Insert content line to the table.
    public static void insertContentLine(String currentTable, String[] contentLine) {
        if (currentTable == null) {
            System.err.println("The table is null.");
            return;
        }
        try {
            List<String> lines = readFile(currentTable);
            int rowIndex;
            if (lines.isEmpty()) {
                // Insert at the beginning when the table is empty.
                rowIndex = 0;
            } else {
                // Insert at the end when the table is not empty.
                rowIndex = lines.size();
            }

            StringBuilder newLine = new StringBuilder();
            for (String singleContent : contentLine) {
                newLine.append(singleContent).append("\t");
            }

            lines.add(rowIndex, newLine.toString());

            writeFile(currentTable, lines);
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't insert the content: " + ioe.getMessage());
        }
    }

    // Modify or add a value to the table.
    public static void modifyTable(String currentTable, String indirectColumnName, String indexValue, String directColumnName, String valueToModify) {
        try {
            List<String> lines = readFile(currentTable);

            // Analyse the table header and indirectly find the index of the column to be modified.
            int basedColumnIndex = getColumnIndex(lines.get(0), indirectColumnName); // Indirect index the value to be modified.
            int modifyColumnIndex = getColumnIndex(lines.get(0), directColumnName); // Direct index the value to be modified.

            // Find the position of the value needs to be modified.
            if (basedColumnIndex != -1 && modifyColumnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Modify and update the table.
                    if (tokens[basedColumnIndex].equals(indexValue)) {
                        while (tokens.length <= modifyColumnIndex) {
                            currentLine += "\t "; // Add tab&space to create empty field.
                            tokens = currentLine.split("\t"); // Update tokens.
                        }
                        tokens[modifyColumnIndex] = String.valueOf(valueToModify);
                        lines.set(i, String.join("\t", tokens));
                        break;
                    }
                }
            }

            writeFile(currentTable, lines);
            System.out.println("[OK]");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Add a new header to the end.
    public static void addNewHeader(String currentTable, String headerName) {
        try {
            List<String> lines = readFile(currentTable);

            // Add the new header name to the end of the header line.
            String headerLine = lines.get(0);
            headerLine += "\t" + headerName;
            lines.set(0, headerLine);

            writeFile(currentTable, lines);
            System.out.println("[OK]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Drop a line from the table.
    public static void dropRow(String currentTable, String directColumnName, String indexValue) {
        try {
            List<String> lines = readFile(currentTable);

            int columnIndex = getColumnIndex(lines.get(0), directColumnName);

            if (columnIndex != -1) {
                for (int i = 1; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    if (tokens.length > columnIndex && tokens[columnIndex].equals(indexValue)) {
                        lines.remove(i);
                        i--; // Adjust index because we removed a line
                    }
                }

                writeFile(currentTable, lines);
                System.out.println("[OK]");
            } else {
                throw new IllegalArgumentException("Column " + directColumnName + " not found.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Drop a column form the table.
    public static void dropColumn(String currentTable, String headerName) {
        try {
            List<String> lines = readFile(currentTable);

            int columnIndex = getColumnIndex(lines.get(0), headerName);

            if (columnIndex != -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] tokens = currentLine.split("\t");
                    // Delete the value at the special column index.
                    if (tokens.length > columnIndex) {
                        List<String> updatedTokens = new ArrayList<>(Arrays.asList(tokens));
                        updatedTokens.remove(columnIndex);
                        lines.set(i, String.join("\t", updatedTokens));
                    }
                }

                writeFile(currentTable, lines);
                System.out.println("[OK]");
            } else {
                System.out.println("Column " + headerName + " not found.");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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

    private static void writeFile(String currentTable, List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(currentTable));
        for (String updatedLine : lines) {
            writer.write(updatedLine);
            writer.newLine();
        }
        writer.close();
    }


}
