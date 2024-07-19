package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataBase0Test {
    private DataBase0 database;
    private String databaseName = "Marks";
    private Table0 table0;
    private String tableName = "marks";
    private String[] titleRow = {"id", "name", "mark", "pass"};
    private String[] insertRow = {"Bob", "21", "@bob.net"};
    private  String contentToDelete = "id";

    @BeforeEach
    public void setupDatabase() {

        database = new DataBase0();
        table0 = new Table0();
    }

    @Test
    public void testCreateDatabase() {
        //TableModification.addNewHeader("databases" + File.separator + "", "add");
        //TableModification.modifyTable("databases" + File.separator + "", "Name", "Chris", "test", "21");
        //TableModification.dropColumn("databases" + File.separator + "", "add");
        //TableModification.dropRow("databases" + File.separator + "", "Name", "Bob");
        database.createDatabase(databaseName);
        table0.createTable(tableName);
        //table.printOutFile();
        //TableModification.insertContentLine(table, titleRow);
        //TableModification.insertContentLine(table, insertRow);
        //TableModification.insertContentLine(table, insertRow);
        //TableModification.addNewHeader(table, "testTitle");
        //TableModification.modifyTable(table, "name", "Bob", "testTitle", "1");
        //TablePrinter.printOutTable(table);
        //TablePrinter.printOUtLine(table, "mark", "21");
        //TablePrinter.printOutLineWithCharacter(table, "name", "bob");//没有结果依旧打印表头
        //TableModification.dropRow(table, "name", "Bob");
        //TableModification.dropColumn(table, "pass");
        //contentTest.deleteContent(contentToDelete);
        //table.dropTable(tableName);
        //database.dropDatabase(databaseName);
    }

    //@Test
    //public void testDropDatabase() { database.dropDatabase(); } individual environment, can't test each.
}