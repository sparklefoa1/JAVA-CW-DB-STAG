package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataBaseTest {
    private DataBase database;
    private String databaseName = "marks";

    @BeforeEach
    public void setDatabase() { database = new DataBase(databaseName); }

    @Test
    public void testDropDatabase() { database.dropDatabase(); }
}
