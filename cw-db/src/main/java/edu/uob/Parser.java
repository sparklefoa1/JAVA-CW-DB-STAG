package edu.uob;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    public static void processTokens(ArrayList<String> tokens) {
        if (containsIgnoreCase(tokens, "USE")) {
            DataBase currentDataBase = new DataBase();
            int index = findIndexIgnoreCase(tokens, "USE");
            String databaseName = tokens.get(index+1);
            databaseName = databaseName.toLowerCase();
            currentDataBase.setStoragePath("databases"+ File.separator + databaseName);
            GlobalObject.getInstance().setDatabase(currentDataBase);
        }
        if (containsIgnoreCase(tokens, "CREATE")) {
            if(findIndexIgnoreCase(tokens, "DATABASE") == -1){
                Table currentTable = new Table();
                int index = findIndexIgnoreCase(tokens, "TABLE");
                String tableName = tokens.get(index+1);
                currentTable.createTable(tableName);
                currentTable.setId(0);
                if(tokens.size() > 4){
                    tokens.remove("(");
                    tokens.remove(",");
                    tokens.remove(")");
                    tokens.remove(";");
                    String[] tokenArray = tokens.subList(3, tokens.size()-1).toArray(new String[0]);
                    TableModification.insertContentLine(currentTable, tokenArray);
                }
            } else {
                DataBase currentDataBase = new DataBase();
                int index = findIndexIgnoreCase(tokens, "DATABASE");
                String databaseName = tokens.get(index + 1);
                currentDataBase.createDatabase(databaseName);
            }

        }
        if (containsIgnoreCase(tokens, "DROP")) {
            if(findIndexIgnoreCase(tokens, "DATABASE") == -1){
                Table currentTable = GlobalObject.getInstance().getTable();
                int index = findIndexIgnoreCase(tokens, "TABLE");
                String tableName = tokens.get(index+1);
                currentTable.dropTable(tableName);
            } else {
                DataBase currentDataBase = GlobalObject.getInstance().getDatabase();
                int index = findIndexIgnoreCase(tokens, "DATABASE");
                String databaseName = tokens.get(index + 1);
                currentDataBase.dropDatabase(databaseName);
            }
        }
        if (containsIgnoreCase(tokens, "ALTER")) {
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = GlobalObject.getInstance().getTable();
            int index = findIndexIgnoreCase(tokens, "TABLE");
            String tableName = tokens.get(index+1);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);

            String headerName = tokens.get(index+3);
            if(tokens.get(index+2).equalsIgnoreCase("ADD")){
                TableModification.addNewHeader(currentTable, headerName);
            } else {
                TableModification.dropColumn(currentTable, headerName);
            }
        }
        if (containsIgnoreCase(tokens, "INSERT")) {
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = new Table();
            int index = findIndexIgnoreCase(tokens, "INTO");
            String tableName = tokens.get(index+1);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);
            currentTable.setId(0);

            if(tokens.size() > 4){
                tokens.remove("(");
                tokens.remove(",");
                tokens.remove(")");
                tokens.remove(";");
                String[] tokenArray = tokens.subList(4, tokens.size()-1).toArray(new String[0]);
                TableModification.insertContentLine(currentTable, tokenArray);
            }
        }
        if (containsIgnoreCase(tokens, "SELECT")) {
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = new Table();
            int index = findIndexIgnoreCase(tokens, "FROM");
            String tableName = tokens.get(index+1);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);

            if(tokens.get(1).equals("*")){
                index = findIndexIgnoreCase(tokens,"WHERE");
                if(index != -1) {
                    String directName = tokens.get(index + 1);
                    if (tokens.get(index + 2).equals("==")) {
                        String indexValue = tokens.get(index + 3);
                        TablePrinter.printOUtLine(currentTable, directName, indexValue);
                    }
                    if (tokens.get(index + 2).equalsIgnoreCase("LIKE")) {
                        String indexValue = tokens.get(index + 3);
                        if (indexValue.startsWith("'") && indexValue.endsWith("'")) {
                            indexValue = indexValue.substring(1, indexValue.length() - 1);
                        }
                        TablePrinter.printOutLineWithCharacter(currentTable, directName, indexValue);
                    }
                    if (tokens.get(index + 2).equals(">")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices2(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.printOUtLine(currentTable, directName, indexValue);
                        }
                    }
                    if (tokens.get(index + 2).equals("<")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices3(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.printOUtLine(currentTable, directName, indexValue);
                        }
                    }
                    if (tokens.get(index + 2).equals(">=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices4(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.printOUtLine(currentTable, directName, indexValue);
                        }
                    }
                    if (tokens.get(index + 2).equals("<=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices5(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.printOUtLine(currentTable, directName, indexValue);
                        }
                    }
                    if (tokens.get(index + 2).equals("!=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices1(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.printOUtLine(currentTable, directName, indexValue);
                        }
                    }
                } else {
                    TablePrinter.printOutTable(currentTable);
                }
            } else {
                String selectName = tokens.get(1);// printer column
                index = findIndexIgnoreCase(tokens,"WHERE");
                if(index != -1) {
                    String directName = tokens.get(index+1);
                    if (tokens.get(index + 2).equals("==")) {
                        String indexValue = tokens.get(index + 3);
                        TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                    }
                    if (tokens.get(index + 2).equalsIgnoreCase("LIKE")) {
                        String indexValue = tokens.get(index + 3);
                        if (indexValue.startsWith("'") && indexValue.endsWith("'")) {
                            indexValue = indexValue.substring(1, indexValue.length() - 1);
                        }
                        TablePrinter.findIndexWithCharacter(currentTable, directName, indexValue, selectName);
                    }
                    if (tokens.get(index + 2).equals(">")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices2(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                        }
                    }
                    if (tokens.get(index + 2).equals("<")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices3(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                        }
                    }
                    if (tokens.get(index + 2).equals(">=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices4(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                        }
                    }
                    if (tokens.get(index + 2).equals("<=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices5(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                        }
                    }
                    if (tokens.get(index + 2).equals("!=")) {
                        int value = Integer.parseInt(tokens.get(index + 3));
                        List<Integer> matchingIndices = findMatchingIndices1(tokens, value);
                        for (Integer index1 : matchingIndices) {
                            String indexValue = tokens.get(index1);
                            TablePrinter.findIndex(currentTable, directName, indexValue, selectName);
                        }
                    }
                } else {
                    TablePrinter.findColumn(currentTable, selectName);
                }
            }
        }
        if (containsIgnoreCase(tokens, "UPDATE")) {
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = new Table();
            int index = findIndexIgnoreCase(tokens, "UPDATE");
            String tableName = tokens.get(index+1);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);

            String indirectName = tokens.get(index+3);
            String indexValue = tokens.get(findIndexIgnoreCase(tokens,"=")+1);
            String directName = tokens.get(findIndexIgnoreCase(tokens, "WHERE")+1);
            String modifyValue = tokens.get(findIndexIgnoreCase(tokens, "==")+1);
            TableModification.modifyTable(currentTable,indirectName, indexValue, directName, modifyValue);
        }
        if (containsIgnoreCase(tokens, "DELETE")) {
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = new Table();
            int index = findIndexIgnoreCase(tokens, "DELETE");
            String tableName = tokens.get(index+2);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);

            index = findIndexIgnoreCase(tokens,"WHERE");
            String directName = tokens.get(index+1);
            if(tokens.get(index+2).equals("==")){
                String indexValue = tokens.get(index+3);
                TableModification.dropRow(currentTable, directName,indexValue);
            }
            if(tokens.get(index+2).equalsIgnoreCase("LIKE")){
                String indexValue = tokens.get(index+3);
                if (indexValue.startsWith("'") && indexValue.endsWith("'")) {
                    indexValue = indexValue.substring(1, indexValue.length() - 1);
                }
                TableModification.dropLineWithCharacter(currentTable, directName, indexValue);
            }
            if(tokens.get(index+2).equals(">")){
                int value = Integer.parseInt(tokens.get(index+3));
                List<Integer> matchingIndices = findMatchingIndices2(tokens, value);
                for (Integer index1 : matchingIndices) {
                    String indexValue = tokens.get(index1);
                    TableModification.dropRow(currentTable,directName, indexValue);
                }
            }
            if(tokens.get(index+2).equals("<")){
                int value = Integer.parseInt(tokens.get(index+3));
                List<Integer> matchingIndices = findMatchingIndices3(tokens, value);
                for (Integer index1 : matchingIndices) {
                    String indexValue = tokens.get(index1);
                    TableModification.dropRow(currentTable,directName, indexValue);
                }
            }
            if(tokens.get(index+2).equals(">=")){
                int value = Integer.parseInt(tokens.get(index+3));
                List<Integer> matchingIndices = findMatchingIndices4(tokens, value);
                for (Integer index1 : matchingIndices) {
                    String indexValue = tokens.get(index1);
                    TableModification.dropRow(currentTable,directName, indexValue);
                }
            }
            if(tokens.get(index+2).equals("<=")){
                int value = Integer.parseInt(tokens.get(index+3));
                List<Integer> matchingIndices = findMatchingIndices5(tokens, value);
                for (Integer index1 : matchingIndices) {
                    String indexValue = tokens.get(index1);
                    TableModification.dropRow(currentTable,directName, indexValue);
                }
            }
            if(tokens.get(index+2).equals("!=")){
                int value = Integer.parseInt(tokens.get(index+3));
                List<Integer> matchingIndices = findMatchingIndices1(tokens, value);
                for (Integer index1 : matchingIndices) {
                    String indexValue = tokens.get(index1);
                    TableModification.dropRow(currentTable,directName, indexValue);
                }
            }

        }
        if (containsIgnoreCase(tokens, "JOIN")) {
            // set talble1.
            DataBase currentDatabase = GlobalObject.getInstance().getDatabase();
            Table currentTable = new Table();
            int index = findIndexIgnoreCase(tokens, "JOIN");
            String tableName = tokens.get(index+1);
            tableName = tableName.toLowerCase();
            currentTable.setStoragePath(currentDatabase.getStoragePath() + File.separator + tableName + ".tab");
            GlobalObject.getInstance().setTable(currentTable);
            //read table1
            //create table3 and add table1 content
            //read table2
            //add content of table2 to table3
        }
    }
    public static List<Integer> findMatchingIndices5(ArrayList<String> tokens, int value) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            try {
                int tokenValue = Integer.parseInt(tokens.get(i));
                if (tokenValue <= value) {
                    matchingIndices.add(i);
                }
            } catch (NumberFormatException e) {
                // Ignore non-integer tokens
            }
        }
        return matchingIndices;
    }
    public static List<Integer> findMatchingIndices4(ArrayList<String> tokens, int value) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            try {
                int tokenValue = Integer.parseInt(tokens.get(i));
                if (tokenValue >= value) {
                    matchingIndices.add(i);
                }
            } catch (NumberFormatException e) {
                // Ignore non-integer tokens
            }
        }
        return matchingIndices;
    }
    public static List<Integer> findMatchingIndices3(ArrayList<String> tokens, int value) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            try {
                int tokenValue = Integer.parseInt(tokens.get(i));
                if (tokenValue < value) {
                    matchingIndices.add(i);
                }
            } catch (NumberFormatException e) {
                // Ignore non-integer tokens
            }
        }
        return matchingIndices;
    }
    public static List<Integer> findMatchingIndices2(ArrayList<String> tokens, int value) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            try {
                int tokenValue = Integer.parseInt(tokens.get(i));
                if (tokenValue > value) {
                    matchingIndices.add(i);
                }
            } catch (NumberFormatException e) {
                // Ignore non-integer tokens
            }
        }
        return matchingIndices;
    }
    public static List<Integer> findMatchingIndices1(ArrayList<String> tokens, int value) {
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            try {
                int tokenValue = Integer.parseInt(tokens.get(i));
                if (tokenValue != value) {
                    matchingIndices.add(i);
                }
            } catch (NumberFormatException e) {
                // Ignore non-integer tokens
            }
        }
        return matchingIndices;
    }
    public static int findIndexIgnoreCase(ArrayList<String> tokens, String targetValue) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).equalsIgnoreCase(targetValue)) {
                return i;
            }
        }
        return -1;
    }
    public static int findIndex(ArrayList<String> tokens, String targetValue) {
        int index = tokens.indexOf(targetValue);
        return (index != -1 && index + 1 < tokens.size()) ? index + 1 : -1;
    }
    public static boolean containsIgnoreCase(ArrayList<String> tokens, String searchString) {
        String searchUpper = searchString.toUpperCase();
        for (String token : tokens) {
            if (token.toUpperCase().equals(searchUpper)) {
                return true;
            }
        }
        return false;
    }
}
