package edu.uob;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Row {
    private final int id; // Can't be changed
    private Map<String, Cell> cells;

    public Row(int id) {
        this.id = id;
        this.cells = new HashMap<>();
    }

    public int getId() {
        return id;
    }
    public void addCell(String columnName, Cell cell) {
        cells.put(columnName, cell);
    }

    public Cell getCell(String columnName) {
        return cells.get(columnName);
    }

    public Collection<Cell> getCells() {
        return cells.values();
    }
}
