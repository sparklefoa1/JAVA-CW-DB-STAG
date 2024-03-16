package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TableTest {
    private Table table;
    private String tableName = "marks";

    @BeforeEach
    public void setupTable() { table = new Table(); }

    @Test
    public void createTable() {table.createTable(tableName);}
}
