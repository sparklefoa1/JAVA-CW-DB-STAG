package edu.uob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CommandParser {
    private Token tokens = new Token();
    private ArrayList<String> commandTokens;

    public CommandParser(String command) {
        this.commandTokens = tokens.setup(command);
    }

    public ArrayList<String> getCommandTokens() {
        return commandTokens;
    }

    public String checkSyntax() throws IOException {
        if (commandTokens.isEmpty()) {
            return "[ERROR]: Command is empty";
        }
        String commandType = commandTokens.get(0).toUpperCase();
        String returnedResult = "[ERROR]: Invalid command type";
        switch (commandType) {
            case "USE":
                return checkUseSyntax();
            case "CREATE":
                return checkCreateSyntax();
            case "DROP":
                return checkDropSyntax();
            case "ALTER":
                return checkAlterSyntax();
            case "INSERT":
                return checkInsertSyntax();
            case "SELECT":
                return checkSelectSyntax();
            case "UPDATE":
                return checkUpdateSyntax();
            case "DELETE":
                return checkDeleteSyntax();
            case "JOIN":
                return checkJoinSyntax();
            default:
                return returnedResult;
        }
    }

    private String checkUseSyntax() throws IOException {
        if(commandTokens.size() == 3 && isPlainText(commandTokens.get(1)) && commandTokens.get(2).equals(";")) {
            // Database name is lowercase in file system
            String databaseName = commandTokens.get(1).toLowerCase();
            try {
                // Setting current database and reading all tables
                DatabaseManager.getInstance().setCurrentDatabase(databaseName);
            } catch (FileNotFoundException | IllegalArgumentException e) {
                return e.getMessage();
            }
            return "[OK]";
        } else {
            return "[ERROR]: Use syntax error";
        }
    }

    private String checkCreateSyntax() throws IOException {
        if (commandTokens.size() < 4) {
            return "[ERROR]: CREATE syntax error";
        }

        String createCommandType = commandTokens.get(1).toUpperCase();
        switch (createCommandType) {
            case "DATABASE":
                return checkCreateDatabaseSyntax();
            case "TABLE":
                return checkCreateTableSyntax();
            default:
                return "[ERROR]: Invalid CREATE command type";
        }
    }

    private String checkCreateDatabaseSyntax() throws IOException {
        if (commandTokens.size() == 4 && isPlainText(commandTokens.get(2)) && commandTokens.get(3).equals(";")) {
            // Saving database name as lowercase
            String databaseName = commandTokens.get(2).toLowerCase();
            // Creating new folder to be current database
            try {
                DatabaseManager.getInstance().createNewDatabase(databaseName);
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            }
            return "[OK]";
        } else {
            return "[ERROR]: Create database syntax error";
        }
    }

    private String checkCreateTableSyntax() throws IOException {
        if (commandTokens.size() >= 4 && isPlainText(commandTokens.get(2))) {
            if (commandTokens.get(3).equals(";")) {
                // Creating tab file as table
                String tableName = commandTokens.get(2).toLowerCase();
                try {
                    DatabaseManager.getInstance().createNewTable(tableName);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    return e.getMessage();
                }
                return "[OK]";
            } else if (commandTokens.get(3).equals("(")
                    && commandTokens.get(commandTokens.size() - 2).equals(")")
                    && commandTokens.get(commandTokens.size() - 1).equals(";")) {
                // Creating tab file as table
                String tableName = commandTokens.get(2).toLowerCase();
                try {
                    DatabaseManager.getInstance().createNewTable(tableName);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    return e.getMessage();
                }
                // Insert id and attribute values == column names
                String attributeList = String.join(" ", commandTokens.subList(4, commandTokens.size() - 2));
                if (isAttributeListValid(attributeList)) {
                    String[] attributes = attributeList.split(",");
                    try {
                        DatabaseManager.getInstance().addAttributesToTable(tableName, attributes);
                    } catch (IllegalArgumentException e) {
                        return e.getMessage();
                    }
                    return "[OK]";
                }
            }
        }
        return "[ERROR]: Create table syntax error";
    }

    private String checkDropSyntax() throws IOException {
        if (commandTokens.size() < 4) {
            return "[ERROR]: DROP syntax error";
        }

        String dropCommandType = commandTokens.get(1).toUpperCase();
        switch (dropCommandType) {
            case "DATABASE":
                return checkDropDatabaseSyntax();
            case "TABLE":
                return checkDropTableSyntax();
            default:
                return "[ERROR]: Invalid DROP command type";
        }
    }

    private String checkDropDatabaseSyntax() throws IOException {
        if (commandTokens.size() == 4 && isPlainText(commandTokens.get(2)) && commandTokens.get(3).equals(";")) {
            String databaseName = commandTokens.get(2).toLowerCase();
            try {
                DatabaseManager.getInstance().deleteDatabase(databaseName);
            } catch (FileNotFoundException | IllegalArgumentException e) {
                return e.getMessage();
            }
            return "[OK]";
        } else {
            return "[ERROR]: Drop database syntax error";
        }
    }

    private String checkDropTableSyntax() throws IOException {
        if (commandTokens.size() == 4 && isPlainText(commandTokens.get(2)) && commandTokens.get(3).equals(";")) {
            String tableName = commandTokens.get(2).toLowerCase();
            try {
                DatabaseManager.getInstance().deleteTable(tableName);
            } catch (IllegalArgumentException | IllegalStateException | FileNotFoundException e) {
                return e.getMessage();
            }
            return "[OK]";
        } else {
            return "[ERROR]: Drop table syntax error";
        }
    }

    private String checkAlterSyntax() {
        if (commandTokens.size() != 6 || !commandTokens.get(1).equalsIgnoreCase("TABLE")) {
            return "[ERROR]: ALTER syntax error";
        }
        // Checking table name
        if (!isPlainText(commandTokens.get(2))) {
            return "[ERROR]: Invalid table name";
        }
        // Checking alter command type
        String alterCommandType = commandTokens.get(3).toUpperCase();
        switch (alterCommandType) {
            case "ADD":
                return checkAlterAddSyntax();
            case "DROP":
                return checkAlterDropSyntax();
            default:
                return "[ERROR]: Invalid ALTER command type";
        }
    }

    private String checkAlterAddSyntax() {
        if (isPlainText(commandTokens.get(4)) && commandTokens.get(5).equals(";")) {
            return "[OK]";
        } else {
            return "[ERROR]: ALTER TABLE ADD syntax error";
        }
    }

    private String checkAlterDropSyntax() {
        if (isPlainText(commandTokens.get(4)) && commandTokens.get(5).equals(";")) {
            return "[OK]";
        } else {
            return "[ERROR]: ALTER TABLE DROP syntax error";
        }
    }

    private String checkInsertSyntax() throws IOException {
        if (commandTokens.size() < 8 ||
                !commandTokens.get(1).equalsIgnoreCase("INTO") ||
                !isPlainText(commandTokens.get(2)) ||
                !commandTokens.get(3).equalsIgnoreCase("VALUES") ||
                !commandTokens.get(4).equals("(") ||
                !commandTokens.get(commandTokens.size() - 2).equals(")") ||
                !commandTokens.get(commandTokens.size() - 1).equals(";")) {
            return "[ERROR]: INSERT syntax error";
        }
        // Checking value list
        String valueList = String.join(" ", commandTokens.subList(5, commandTokens.size() - 2));
        if (isValueListValid(valueList)) {
            // Writing values into attributes
            String tableName = commandTokens.get(2);
            String[] values = valueList.split(",");
            try {
                DatabaseManager.getInstance().insertIntoTable(tableName, values);
            } catch (IllegalStateException | FileNotFoundException | IllegalArgumentException e) {
                return e.getMessage();
            }
            return "[OK]";
        }
        return "[ERROR]: Invalid value list";
    }

    // pending
    private String checkSelectSyntax() throws IOException {
        if (commandTokens.size() < 5 || !commandTokens.get(commandTokens.size() - 1).equals(";")) {
            return "[ERROR]: SELECT syntax error";
        }

        int fromIndex = -1;
        for (int i = 0; i < commandTokens.size(); i++) {
            if (commandTokens.get(i).equalsIgnoreCase("FROM")) {
                fromIndex = i;
                break;
            }
        }
        if (fromIndex < 2 || fromIndex >= commandTokens.size() - 2) {
            return "[ERROR]: SELECT syntax error: FROM";
        }

        String wildAttribList = String.join(" ", commandTokens.subList(1, fromIndex));
        if (!isWildAttribListValid(wildAttribList)) {
            return "[ERROR]: Invalid WildAttribList";
        }

        String tableName = commandTokens.get(fromIndex + 1);
        if (!isPlainText(tableName)) {
            return "[ERROR]: Invalid TableName";
        }

        int whereIndex = commandTokens.indexOf("WHERE");
        if (whereIndex != -1) {
            if (whereIndex != fromIndex + 2 || whereIndex >= commandTokens.size() - 4) {
                return "[ERROR]: SELECT syntax error: missing WHERE";
            }
            String condition = String.join(" ", commandTokens.subList(whereIndex + 1, commandTokens.size() - 1));
            if (!isConditionValid(condition)) {
                return "[ERROR]: Invalid Condition";
            }
        } else if (commandTokens.size() != fromIndex + 3) {
            return "[ERROR]: SELECT syntax error";
        } else {
            // There are no conditions
            try {
                return "[OK]" + "\n" + DatabaseManager.getInstance().selectColumnsFromTable(tableName, wildAttribList);
            } catch (IllegalStateException | IllegalArgumentException | FileNotFoundException e) {
                return e.getMessage();
            }
        }
        // There are conditions

        return "[OK]";
    }

    private boolean isConditionValid(String condition) {
        String trimmedCondition = condition.trim();

        // Check ()
        if (trimmedCondition.startsWith("(") && trimmedCondition.endsWith(")")) {
            String innerCondition = trimmedCondition.substring(1, trimmedCondition.length() - 1).trim();
            if (isConditionValid(innerCondition)) {
                return true;
            }
        }

        // Check AND or OR
        String[] boolOperators = {" AND ", " OR "};
        for (String operator : boolOperators) {
            int operatorIndex = trimmedCondition.indexOf(operator);
            if (operatorIndex != -1) {
                String leftCondition = trimmedCondition.substring(0, operatorIndex).trim();
                String rightCondition = trimmedCondition.substring(operatorIndex + operator.length()).trim();
                if (isConditionValid(leftCondition) && isConditionValid(rightCondition)) {
                    return true;
                }
            }
        }

        // Check conditions: attrName comparator value
        String[] parts = trimmedCondition.split("\\s+");
        if (parts.length == 3) {
            return isPlainText(parts[0]) && isComparator(parts[1]) && isValue(parts[2]);
        }

        return false;
    }


    private String checkUpdateSyntax() {
        if (commandTokens.size() < 7) {
            return "[ERROR]: UPDATE syntax error";
        }

        int setIndex = commandTokens.indexOf("SET");
        if (setIndex == -1 || setIndex < 2 || setIndex >= commandTokens.size() - 2) {
            return "[ERROR]: UPDATE syntax error 1";
        }

        int whereIndex = commandTokens.indexOf("WHERE");
        if (whereIndex == -1 || whereIndex <= setIndex + 1 || whereIndex >= commandTokens.size() - 4) {
            return "[ERROR]: UPDATE syntax error 2";
        }

        String tableName = commandTokens.get(1);
        if (!isPlainText(tableName)) {
            return "[ERROR]: Invalid TableName";
        }

        String nameValueList = String.join(" ", commandTokens.subList(setIndex + 1, whereIndex));
        if (!isNameValueListValid(nameValueList)) {
            return "[ERROR]: Invalid NameValueList";
        }

        String condition = String.join(" ", commandTokens.subList(whereIndex + 1, commandTokens.size() - 1));
        if (!isConditionValid(condition)) {
            return "[ERROR]: Invalid Condition";
        }

        return "[OK]";
    }

    private boolean isNameValueListValid(String nameValueList) {
        String[] pairs = nameValueList.split(",");
        for (String pair : pairs) {
            if (!isNameValuePairValid(pair.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean isNameValuePairValid(String pair) {
        String[] parts = pair.split("=");
        if (parts.length != 2) {
            return false;
        }
        return isPlainText(parts[0].trim()) && isValue(parts[1].trim());
    }

    private String checkDeleteSyntax() {
        if (commandTokens.size() < 6) {
            return "[ERROR]: DELETE syntax error";
        }

        if (!commandTokens.get(1).equalsIgnoreCase("FROM")) {
            return "[ERROR]: DELETE syntax error 1";
        }

        String tableName = commandTokens.get(2);
        if (!isPlainText(tableName)) {
            return "[ERROR]: Invalid TableName";
        }

        if (!commandTokens.get(3).equalsIgnoreCase("WHERE")) {
            return "[ERROR]: DELETE syntax error 2";
        }

        String condition = String.join(" ", commandTokens.subList(4, commandTokens.size() - 1));
        if (!isConditionValid(condition)) {
            return "[ERROR]: Invalid Condition";
        }

        return "[OK]";
    }

    private String checkJoinSyntax() {
        if (commandTokens.size() != 9) {
            return "[ERROR]: JOIN syntax error";
        }

        if (!isPlainText(commandTokens.get(1))) {
            return "[ERROR]: Invalid first TableName";
        }

        if (!commandTokens.get(2).equalsIgnoreCase("AND")) {
            return "[ERROR]: Expected 'AND' after first TableName";
        }

        if (!isPlainText(commandTokens.get(3))) {
            return "[ERROR]: Invalid second TableName";
        }

        if (!commandTokens.get(4).equalsIgnoreCase("ON")) {
            return "[ERROR]: Expected 'ON' after second TableName";
        }

        if (!isPlainText(commandTokens.get(5))) {
            return "[ERROR]: Invalid first AttributeName";
        }

        if (!commandTokens.get(6).equalsIgnoreCase("AND")) {
            return "[ERROR]: Expected 'AND' after first AttributeName";
        }

        if (!isPlainText(commandTokens.get(7))) {
            return "[ERROR]: Invalid second AttributeName";
        }

        return "[OK]";
    }

    private boolean isPlainText(String token) {
        return token.matches("[a-zA-Z0-9]+");
    }

    private boolean isWildAttribListValid(String wildAttribList) {
        if (wildAttribList.equals("*")) {
            return true;
        }
        return isAttributeListValid(wildAttribList);
    }

    private boolean isAttributeListValid(String attributeList) {
        String[] attributes = attributeList.split(",");
        for (String attribute : attributes) {
            if (!isPlainText(attribute.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean isValueListValid(String valueList) {
        String[] values = valueList.split(",");
        for (String value : values) {
            if (!isValue(value.trim())) {
                return false;
            }
        }
        return true;
    }

    private boolean isValue(String token) {
        return isStringLiteral(token) || isBooleanLiteral(token) || isFloatLiteral(token) || isIntegerLiteral(token) || isNullLiteral(token);
    }
    private boolean isStringLiteral(String token) {
        // BNF syntax
        return token.matches("'([\\sA-Za-z!#\\\\$%&()*+,\\-./:;>=<?@\\[\\]^_`{}~0-9]*)'");

    }

    private boolean isBooleanLiteral(String token) {
        return token.equalsIgnoreCase("TRUE") || token.equalsIgnoreCase("FALSE");
    }

    private boolean isFloatLiteral(String token) {
        return token.matches("[-+]?\\d*\\.\\d+");
    }

    private boolean isIntegerLiteral(String token) {
        return token.matches("[-+]?\\d+");
    }

    private boolean isNullLiteral(String token) {
        return token.equalsIgnoreCase("NULL");
    }

    private boolean isComparator(String token) {
        return token.equals("==") || token.equals(">") || token.equals("<") ||
                token.equals(">=") || token.equals("<=") || token.equals("!=") ||
                token.equalsIgnoreCase("LIKE");
    }

}
