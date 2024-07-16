package edu.uob;

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

    public String checkSyntax() {
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
            /*case "DROP":
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
                return checkJoinSyntax();*/
            default:
                return returnedResult;
        }
    }

    private String checkUseSyntax() {
        if(commandTokens.size() == 3 && isPlainText(commandTokens.get(1)) && commandTokens.get(2).equals(";")) {
            return "[OK]";
        } else {
            return "[ERROR]: Use syntax error";
        }
    }

    private String checkCreateSyntax() {
        if (commandTokens.size() < 4) {
            return "[ERROR]: Create syntax error";
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

    private String checkCreateDatabaseSyntax() {
        if (commandTokens.size() == 4 && isPlainText(commandTokens.get(2)) && commandTokens.get(3).equals(";")) {
            return "[OK]";
        } else {
            return "[ERROR]: Create database syntax error";
        }
    }

    private String checkCreateTableSyntax() {
        if (commandTokens.size() >= 4 && isPlainText(commandTokens.get(2))) {
            if (commandTokens.get(3).equals(";")) {
                return "[OK]";
            } else if (commandTokens.get(3).equals("(")
                    && commandTokens.get(commandTokens.size() - 2).equals(")")
                    && commandTokens.get(commandTokens.size() - 1).equals(";")) {
                String attributeList = String.join(" ", commandTokens.subList(4, commandTokens.size() - 2));
                if (isAttributeListValid(attributeList)) {
                    return "[OK]";
                }
            }
        }
        return "[ERROR]: Create table syntax error";
    }

    private boolean isPlainText(String token) {
        return token.matches("[a-zA-Z0-9]+");
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

    private boolean isValue(String token) {
        return token.matches("'[^']*'") || token.matches("TRUE|FALSE") || token.matches("[+-]?\\d+(\\.\\d+)?") || token.equals("NULL");
    }
}
