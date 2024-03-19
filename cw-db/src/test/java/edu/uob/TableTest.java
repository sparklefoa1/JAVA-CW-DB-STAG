package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableTest {
    private Table table;
    private Row printTest;
    private String tableName = "marks";
    private String[] titleRow = {"id", "name", "mark", "pass"};

    @BeforeEach
    public void setupTable() { table = new Table(); printTest = new Row();}

    @Test
    public void createTable() {table.createTable(tableName);}

    // Check if the spreadsheet file can be read and printed normally.
    @Test
    public void testPrintOutFile(){
        printTest.printOutFile();
        printTest.insertRow(titleRow);
    }
}
