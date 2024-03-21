package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    public static void CommandType(String[] commandName)
    {
        // Convert to Capital letter.
        for (int i = 0; i < commandName.length; i++) {
            commandName[i] = commandName[i].toUpperCase();
        }

        int i = 0;
        if(commandName[i] == "USE"){
            i++;
            String databaseName = commandName[i];
            DataBase dataBase = new DataBase();
            dataBase.setStoragePath("databases"+ File.separator + databaseName);
            GlobalObject.getInstance().setDatabase(dataBase);
            return;
        }
        if(commandName[i] == "CREATE"){//i++要记得防止超界限
            i++;
            String createType = commandName[i];
            if(createType == "DATABASE"){
                i++;
                String databaseName = commandName[i];
                DataBase dataBase = new DataBase();
                dataBase.createDatabase(databaseName);
                return;
            } else if(createType == "TABLE"){
                i++;
                String tableName = commandName[i];
                Table table = new Table();
                table.createTable(tableName);
                i++;
                if(commandName[i] == "("){
                    ArrayList<String> initialTableValue = new ArrayList<>();
                    int j = i + 1;
                    while(!(commandName[j] == ")")){
                        initialTableValue.add(String.valueOf(commandName[j]));
                        j++;
                    }
                    String[] initialTableValueArray = new String[initialTableValue.size()];
                    initialTableValueArray = initialTableValue.toArray(initialTableValueArray);
                    TableModification.insertContentLine(table, initialTableValueArray);
                    return;
                }
                return;
            }
            return;
        }
        if(commandName[i] == "DROP"){
            i++;
            String dropType = commandName[i];
            if(dropType == "DATABASE"){
                i++;
                String databaseName = commandName[i];
                DataBase dataBase = new DataBase();
                dataBase.setStoragePath("databases"+ File.separator + databaseName);
                dataBase.dropDatabase(databaseName);//需要修改为传入database对象的模式 or 路径重新设置
            } else if(dropType == "TABLE"){
                i++;
                String tableName = commandName[i];
                Table table = new Table();
                table.dropTable(tableName);//同上
            }
            return;
        }
        if(commandName[i] == "ALTER")){
            i++;
            if(commandName[i] == "TABLE"){
                i++;
                String tableName = commandName[i];
                //set table by tablename
                Table table = new Table();
                table = GlobalObject.getInstance().getTable();
                i++;
                if(commandName[i] == "ADD"){
                    i++;
                    TableModification.addNewHeader(table, commandName[i]);
                }else if(commandName[i] == "DROP"){
                    i++;
                    TableModification.dropColumn(table, commandName[i]);
                }
            }
            else{
                //ERROR 语法错误
            }
            return;
        }
        if(commandName[i].equals("INSERT")){
            i++;
            if(commandName[i].equals("INTO")){
                i++;
                String tableName = commandName[i];
                //set table by tablename
                Table table = new Table();
                table = GlobalObject.getInstance().getTable();
            }
            else {
                //报错
            }

        ERROR("Expecting an instruction?");
    }
}
