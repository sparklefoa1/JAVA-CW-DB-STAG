package edu.uob;

public class GlobalObject {
    private static GlobalObject Instance;
    private DataBase0 database;
    private Table0 table0;

    private GlobalObject() {
        // Make sure that only one instance exists throughout the entire package.
    }

    public static GlobalObject getInstance() {
        if (Instance == null) {
            Instance = new GlobalObject();
        }
        return Instance;
    }

    public void setDatabase(DataBase0 database) {
        this.database = database;
    }

    public DataBase0 getDatabase() {
        return database;
    }

    public void setTable(Table0 table0) {
        this.table0 = table0;
    }

    public Table0 getTable() {
        return table0;
    }
}
