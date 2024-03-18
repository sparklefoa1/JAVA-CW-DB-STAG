package edu.uob;

public class PathManager {
    private static PathManager pathInstance;
    private String databaseStoragePath;
    private String tableStoragePath;

    private PathManager() {
        // Make sure that only one PathManager instance exists throughout the entire package.
    }

    public static PathManager getPathInstance() {
        if (pathInstance == null) {
            pathInstance = new PathManager();
        }
        return pathInstance;
    }

    public void setDatabaseStoragePath(String folderPath) { this.databaseStoragePath = folderPath; System.out.println(this.databaseStoragePath);}

    public String getDatabaseStoragePath() { return databaseStoragePath; }
    public void setTableStoragePath(String folderPath) { this.tableStoragePath = folderPath;}

    public String getTableStoragePath() { return tableStoragePath; }
}

