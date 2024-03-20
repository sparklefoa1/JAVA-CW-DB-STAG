package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TableModification {

    public void findPosition(String columnToModify) {//或者叫title什么什么?
        // 解析表头，确定要修改的列的索引
        String[] header = lines.get(0).split("\t");
        int columnIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(columnToModify)) {
                columnIndex = i;
                break;
            }
        }
    }
    public void modifyTable(String currentTable, String contentToFind, String contentToModify) {
        try {
            // Read table contents and store in list.
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(currentTable));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            // 在列表中搜索指定名称，并修改 mark 值
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split("\t");
                // 用方法找到要修改的行
                if (parts.length >= 4 && parts[1].equals(contentToFind)) {
                    parts[2] = String.valueOf(contentToModify);
                    lines.set(i, String.join("\t", parts));
                    found = true;
                    break;
                }
            }

            // 如果找到了指定名称，则写回文件
            if (found) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentTable));
                for (String updatedLine : lines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
                writer.close();
                System.out.println("Mark for " + contentToFind + " has been updated to " + contentToModify);
            } else {
                System.out.println("Name not found: " + contentToFind);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
