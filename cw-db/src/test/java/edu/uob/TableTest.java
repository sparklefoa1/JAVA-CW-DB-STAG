package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableTest {
    private Table table;
    private Insertion printTest;
    private String tableName = "marks";
    private String[] titleRow = {"id", "name", "mark", "pass"};
    private  String contentToDelete = "id";

    @BeforeEach
    public void setupTable() { table = new Table(); printTest = new Insertion();}

    @Test
    public void createTable() {table.createTable(tableName);}

    // Check if the spreadsheet file can be read and printed normally.
    @Test
    public void testPrintOutFile(){
        //printTest.insertContent(titleRow);//second will in the same line
        printTest.deleteContent(contentToDelete);
    }
}
