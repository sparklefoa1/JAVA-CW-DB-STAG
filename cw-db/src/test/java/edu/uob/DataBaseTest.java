package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class DataBaseTest {
    private DataBase database;
    private String databaseName = "Marks";
    private Table table;
    private String tableName = "marks";
    private Insertion contentTest;
    private String[] titleRow = {"id", "name", "mark", "pass"};
    private  String contentToDelete = "id";

    @BeforeEach
    public void setupDatabase() {
        database = new DataBase();
        table = new Table();
        contentTest = new Insertion();
    }

    @Test
    public void testCreateDatabase() {
        database.createDatabase(databaseName);
        table.createTable(tableName);
        //table.printOutFile();
        contentTest.insertContent(titleRow);//second will in the same line
        //contentTest.deleteContent(contentToDelete);
        //table.dropTable(tableName);
        //database.dropDatabase(databaseName);
    }

    //@Test
    //public void testDropDatabase() { database.dropDatabase(); } individual environment, can't test each.
}
