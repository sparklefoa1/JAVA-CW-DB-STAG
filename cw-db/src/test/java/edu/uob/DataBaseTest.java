package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataBaseTest {
    private DataBase database;
    private String databaseName = "Marks";
    private Table table;
    private String tableName = "marks";

    @BeforeEach
    public void setupDatabase() { database = new DataBase(); table = new Table();}

    @Test
    public void testCreateDatabase() {
        database.createDatabase(databaseName);
        table.createTable(tableName);//repeate create cant susseful
        //database.dropDatabase(databaseName);
    }

    //@Test
    //public void testDropDatabase() { database.dropDatabase(); } individual environment, can't test each.
}
