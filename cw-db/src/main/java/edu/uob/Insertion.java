package edu.uob;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;

public class Insertion {

    public void insertContent(String[] contentValue) {
        //String currentTable = PathManager.getPathInstance().getTableStoragePath();
        String currentTable = "databases" + File.separator + "marks" + File.separator + "marks.tab";
        if (currentTable == null) {
            System.err.println("The tablePath is null.");
            return;
        }
        try (BufferedWriter insertValue = new BufferedWriter(new FileWriter(currentTable, true))) {
            for (String singleContent : contentValue) {
                // Use Unicode characters to represent tab characters.
                insertValue.write(singleContent + "\t");
            }
            insertValue.newLine();
            insertValue.close();//still need?
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't insert the title row: " + currentTable);
        }
    }

    public void deleteContent(String contentToDelete) {
        //String currentTable = PathManager.getPathInstance().getTableStoragePath();
        String currentTable = "databases" + File.separator + "marks" + File.separator + "marks.tab";
        if (currentTable == null) {
            System.err.println("The tablePath is null.");
            return;
        }
        File inputTable = new File(currentTable);
        File tempTable = new File(currentTable + ".tmp");
        try  (BufferedReader reader = new BufferedReader(new FileReader(inputTable));
              BufferedWriter writer = new BufferedWriter(new FileWriter(tempTable))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                // Check if the line contains the content to delete, if yes, skip it.
                if (!currentLine.contains(contentToDelete)) {
                    writer.write(currentLine);
                    writer.newLine(); // Add a new line after writing the line.
                }

            }
        }  catch (IOException ioe) {
            System.err.println("Error reading or writing file: ");
        }
        try {
            java.nio.file.Files.delete(java.nio.file.Paths.get(currentTable));
            java.nio.file.Files.move(java.nio.file.Paths.get(currentTable + ".tmp"), java.nio.file.Paths.get(currentTable));
        } catch (IOException ioe) {
            System.err.println("Unable to delete or rename file: " + ioe.getMessage());
        }
    }

}
