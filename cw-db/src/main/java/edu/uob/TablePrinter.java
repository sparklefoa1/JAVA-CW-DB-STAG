package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TablePrinter {
    public static void printOutFile(Table currentTable) {//use tableName?
        String tablePath = currentTable.getStoragePath();
        try {
            FileReader reader = new FileReader(tablePath);
            BufferedReader bufferReader = new BufferedReader(reader);
            String tableFile;
            while ((tableFile = bufferReader.readLine()) != null) {
                System.out.println(tableFile);
            }
            bufferReader.close();
            reader.close();
        } catch (IOException ioe) {
            System.out.println("Can't read this file: " + tablePath);
        }
    }
}
