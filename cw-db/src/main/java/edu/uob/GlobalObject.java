package edu.uob;

public class GlobalObject {
    private static GlobalObject Instance;
    private DataBase database;
    private Table table;

    private GlobalObject() {
        // Make sure that only one instance exists throughout the entire package.
    }

    public static GlobalObject getInstance() {
        if (Instance == null) {
            Instance = new GlobalObject();
        }
        return Instance;
    }

    public void setDatabase(DataBase database) {
        this.database = database;
    }

    public DataBase getDatabase() {
        return database;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }
}
