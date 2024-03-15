package edu.uob;

public class PathManager {
    private static PathManager pathInstance;
    private String databaseFolderPath;

    private PathManager() {
        // Make sure that only one PathManager instance exists throughout the entire package.
    }

    public static PathManager getPathInstance() {
        if (pathInstance == null) {
            pathInstance = new PathManager();
        }
        return pathInstance;
    }

    public void setDatabaseFolderPath(String folderPath) { this.databaseFolderPath = folderPath; }

    public String getDatabaseFolderPath() { return databaseFolderPath; }
}

