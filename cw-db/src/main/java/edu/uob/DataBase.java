package edu.uob;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataBase {
    private String storageFolderPath;
    public DataBase(String DBname) {
        storageFolderPath = Paths.get("databases" + File.separator + DBname).toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
            System.out.println("[OK]");
        } catch(IOException ioe) {
            System.out.println("Can't seem to create a database: " + storageFolderPath);
        }
    }

    public void dropDatabase() {
        try {
            // Delete database folder
            Files.deleteIfExists(Paths.get(storageFolderPath));
            System.out.println("[OK]");
        } catch (IOException ioe) {
            System.out.println("Failed to delete database folder: " + storageFolderPath);
        }
    }

    /* public String DropDataBase(String DBname) {
        if(DBname.isDirectory()) {
            // 获取文件夹中的所有文件和子文件夹
            File[] files = DBname.listFiles();
            if(files != null) {
                for (File file : files) {
                            // 递归删除子文件夹和文件
                    deleteFolder(file);
                }
            }
        }
                // 删除文件夹本身
        if (!folder.delete()) {
            System.err.println("Failed to delete folder: " + folder.getAbsolutePath());
        } else {
            System.out.println("Folder deleted: " + folder.getAbsolutePath());
        }
    }*/
}
