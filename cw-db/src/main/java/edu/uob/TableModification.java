package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TableModification {

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
                    // Prevent array index from going out of bounds.
                    // Modify and update the table.
                    if (tokens.length > basedColumnIndex && tokens.length > modifyColumnIndex && tokens[basedColumnIndex].equals(indexValue)) {
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
