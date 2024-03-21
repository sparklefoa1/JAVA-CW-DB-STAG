package edu.uob;

import java.util.ArrayList;

public class SyntexCheck {
    void command(ArrayList<String> tokens) throws Exception {

        if(!tokens.get(tokens.size() - 1).equals(";")){
            throw new Exception("[ERROR]: Semi colon missing at end of line.");
        }
        commandTypeCheck(tokens);
    }

    void commandTypeCheck(ArrayList<String> tokens) throws Exception {
        String commandType = tokens.get(0);
        commandType = commandType.toUpperCase();
        if(commandType.equals("USE")){
            String databaseName = tokens.get(1);
            if(!PlainTextCheck(databaseName)){
                throw new Exception("[ERROR]: DatabaseName is invalid.");
            }
        }
        if(commandType.equals("CREATE")){
            String createType = tokens.get(1);
            createType = createType.toUpperCase();
            if(createType.equals("DATABASE") || createType.equals("TABLE")) {
                String createName = tokens.get(2);
                if(!PlainTextCheck(createName)){
                    throw new Exception("[ERROR]: CreateName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: CreateType is invalid.");
            }
        }
        if(commandType.equals("DROP")){
            String dropType = tokens.get(1);
            dropType = dropType.toUpperCase();
            if(dropType.equals("DATABASE") || dropType.equals("TABLE")) {
                String dropName = tokens.get(2);
                if(!PlainTextCheck(dropName)){
                    throw new Exception("[ERROR]: DropName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: DropType is invalid.");
            }
        }
        if(commandType.equals("ALTER")){
            String alterTable = tokens.get(1);
            alterTable = alterTable.toUpperCase();
            if(alterTable.equals("TABLE")) {
                String tableName = tokens.get(2);
                if(PlainTextCheck(tableName)){
                    String alterType = tokens.get(3);
                    alterType = alterType.toUpperCase();
                    if(alterType.equals("ADD") || alterType.equals("DROP")) {
                        String attributeName = tokens.get(4);
                        if(!PlainTextCheck(attributeName)){
                            throw new Exception("[ERROR]: AttributeName is invalid.");
                        }
                    } else {
                        throw new Exception("[ERROR]: AlterType is invalid.");
                    }
                } else {
                    throw new Exception("[ERROR]: TableName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: AlterObject is invalid.");
            }
        }
        if(commandType.equals("INSERT")){
            String text = tokens.get(1);
            text = text.toUpperCase();
            if(text.equals("INTO")) {
                String tableName = tokens.get(2);
                if(PlainTextCheck(tableName)){
                    String keyWord = tokens.get(3);
                    keyWord = keyWord.toUpperCase();
                    if(keyWord.equals("VALUES")) {
                        if(tokens.get(4).equals("(") && tokens.get(tokens.size()-2).equals(")")) {
                            valueListCheck(tokens, 4);
                        } else {
                            throw new Exception("[ERROR]: Syntax '(' or ')' is missing.");
                        }
                    } else {
                        throw new Exception("[ERROR]: KeyWord 'VALUES' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: TableName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: Syntax is invalid.");
            }
        }
    }

    void valueListCheck(ArrayList<String> tokens, int index) throws Exception {
        if(!tokens.get(index+2).equals(",")){
            valueCheck(tokens.get(index+1));
            return;
        }
        valueCheck(tokens.get(index+1));
        index = index + 2;
        valueListCheck(tokens, index);
    }

    void valueCheck(String value) throws Exception {
        if(value.startsWith("'") && value.endsWith("'")){
            return;
        }
        if(value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE")){
            return;
        }
        if(value.matches("^[0-9+-.]+$")){
            return;
        }
        if(value.equalsIgnoreCase("NULL")){
            return;
        }
        throw new Exception("[ERROR]: Value is invalid.");
    }

    boolean PlainTextCheck(String text){
        return text.matches("[a-zA-Z0-9]+");
    }
}
