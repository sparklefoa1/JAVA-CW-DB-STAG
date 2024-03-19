package edu.uob;

import java.io.*;

public class Row {

    public Row() {}

    public void insertRow(String[] contentRow) {
        String currentTable = PathManager.getPathInstance().getTableStoragePath();//if this is null?
        //String currentTable = "databases" + File.separator + "marks" + File.separator + "marks.tab";
        if (currentTable == null) {
            System.err.println("The tablePath is null.");
            return;
        }
        try (BufferedWriter insertLine = new BufferedWriter(new FileWriter(currentTable, true))) {
            for (String valueContent : contentRow) {
                // Use Unicode characters to represent tab characters.
                insertLine.write(valueContent + "\u0009");
            }
            insertLine.close();
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Can't insert the title row: " + currentTable);
        }
    }

    public void printOutFile() {
        String filePath ="databases" + File.separator + "people.tab";
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader bufferReader = new BufferedReader(reader);
            String tableFile;
            while ((tableFile = bufferReader.readLine()) != null) {
                System.out.println(tableFile);
            }
            bufferReader.close();
            reader.close();
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + filePath);
        }
    }
}
