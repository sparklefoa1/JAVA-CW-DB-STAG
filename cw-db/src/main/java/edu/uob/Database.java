package edu.uob;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private String name;
    private Map<String, Table> tables;

    public Database(String name) {
        this.name = name;
        this.tables = new HashMap<>();
    }

    public void addTable(Table table) {
        tables.put(table.getName(), table);
    }

    public void removeTable(String tableName) {
        tables.remove(tableName);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public Collection<Table> getTables() {
        return tables.values();
    }

    public String getName() {
        return name;
    }
}
