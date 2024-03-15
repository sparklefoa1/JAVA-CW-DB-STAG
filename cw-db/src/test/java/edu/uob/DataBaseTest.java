package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataBaseTest {
    private DataBase database;
    private String databaseName = "marks";

    @BeforeEach
    public void setupDatabase() { database = new DataBase(); }

    @Test
    public void testCreateDatabase() {
        database.createDatabase(databaseName);
        database.dropDatabase();
    }

    //@Test
    //public void testDropDatabase() { database.dropDatabase(); } individual environment, can't test each.
}
