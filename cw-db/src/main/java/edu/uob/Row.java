package edu.uob;

import java.io.*;

public class Row {

    public Row() {}

    public void insertRow(String[] contentRow) {
        //String currentTable = PathManager.getPathInstance().getTableStoragePath();
        String currentTable = "databases" + File.separator + "marks" + File.separator + "marks.tab";
        try {
            FileWriter titleRow = new FileWriter(currentTable);
            titleRow.write(contentRow[0]);
            for (int i = 1; i < contentRow.length; i++) {
                titleRow.write("\t" + contentRow[i]);
            }
            //titleRow.newLine(); need Bufferwritter method
            titleRow.close();
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
