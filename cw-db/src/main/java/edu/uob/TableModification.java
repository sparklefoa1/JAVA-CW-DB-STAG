package edu.uob;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TableModification {
    public void insertContent(String currentTable, ArrayList<String> contentValue) {
        int insertIndex;
        insertContent(currentTable, 0, contentValue);
    }
    public void insertContent(String currentTable, int insertIndex, ArrayList<String> contentValue) {
        if (currentTable == null) {
            System.err.println("The tablePath is null.");
            return;
        }
        try (BufferedWriter insertValue = new BufferedWriter(new FileWriter(currentTable, true))) {
            StringBuilder line = new StringBuilder();
            for (String singleContent : contentValue) {
                // Use Unicode characters to represent tab characters.
                line.append(singleContent).append("\u0009");
            }
            insertValue.write(line.toString());
            insertValue.newLine();
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't insert the value: " + currentTable);
        }
    }
}
