package edu.uob;

import java.util.ArrayList;

public class SyntaxCheck {
    public static void command(ArrayList<String> tokens) throws Exception {

        if(!tokens.get(tokens.size() - 1).equals(";")){
            throw new Exception("[ERROR]: Semi colon missing at end of line.");
        }
        commandTypeCheck(tokens);
    }

    public static void commandTypeCheck(ArrayList<String> tokens) throws Exception {
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
        if(commandType.equals("SELECT")){
            int index = 1;
            String wildAttribList = tokens.get(1);
            if(!wildAttribList.equals("*")){
                index = attribListCheck(tokens, 0);
            }
            if(tokens.get(index+1).equalsIgnoreCase("FROM")){
                String tablename = tokens.get(index+2);
                if(PlainTextCheck(tablename)){
                    if(tokens.get(index+3).equalsIgnoreCase("WHERE")){
                        index = index + 4;
                        conditionCheck(tokens, index);
                    } else if(!tokens.get(index+4).equals(";")) {
                        throw new Exception("[ERROR]: KeyWord 'WHERE' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: TableName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: KeyWord 'FROM' is missing.");
            }
        }
        if(commandType.equals("UPDATE")){
            String tablename = tokens.get(1);
            if(PlainTextCheck(tablename)) {
                if(tokens.get(2).equalsIgnoreCase("SET")){
                    int index = 6;
                    index = nameValueListCheck(tokens, 2);
                    if(tokens.get(index).equalsIgnoreCase("WHERE")){
                        conditionCheck(tokens, index+1);
                    } else {
                        throw new Exception("[ERROR]: KeyWord 'WHERE' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: KeyWord 'SET' is missing.");
                }
            } else {
                throw new Exception("[ERROR]: TableName is invalid.");
            }
        }
        if(commandType.equals("DELETE")){
            if(tokens.get(1).equalsIgnoreCase("FROM")){
                String tableName = tokens.get(2);
                if(PlainTextCheck(tableName)){
                    if(tokens.get(3).equalsIgnoreCase("WHERE")){
                        conditionCheck(tokens, 4);
                    } else {
                        throw new Exception("[ERROR]: KeyWord 'WHERE' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: TableName is invalid.");
                }
            } else {
                throw new Exception("[ERROR]: KeyWord 'FROM' is missing.");
            }
        }
        if(commandType.equals("JOIN")){
            String tablename1 = tokens.get(1);
            if(PlainTextCheck(tablename1)) {
                if(tokens.get(2).equalsIgnoreCase("AND")){
                    String tablename2 = tokens.get(3);
                    if(PlainTextCheck(tablename2)){
                        if(tokens.get(4).equalsIgnoreCase("ON")){
                            String attributeName1 = tokens.get(5);
                            if(PlainTextCheck(attributeName1)){
                                if(tokens.get(6).equalsIgnoreCase("AND")){
                                    String attributeName2 = tokens.get(7);
                                    if(!PlainTextCheck(attributeName2)) {
                                        throw new Exception("[ERROR]: AttributeName2 is invalid.");
                                    }
                                } else {
                                    throw new Exception("[ERROR]: KeyWord 'AND' is missing.");
                                }
                            } else {
                                throw new Exception("[ERROR]: AttributeName1 is invalid.");
                            }
                        } else {
                            throw new Exception("[ERROR]: KeyWord 'ON' is missing.");
                        }
                    } else {
                        throw new Exception("[ERROR]: TableName1 is invalid.");
                    }
                } else {
                    throw new Exception("[ERROR]: KeyWord 'AND' is missing.");
                }
            } else {
                throw new Exception("[ERROR]: TableName1 is invalid.");
            }
        }
    }
    public static int nameValueListCheck(ArrayList<String> tokens, int index) throws Exception {
        if(tokens.get(index+2).equals(",")){
            nameValuePairCheck(tokens, index+1);
            index = index + 2;
            nameValueListCheck(tokens, index);
        }
        nameValuePairCheck(tokens, index+1);
        return index+4;
    }
    public static void nameValuePairCheck(ArrayList<String> tokens, int index) throws Exception {
        if(PlainTextCheck(tokens.get(index))){
            if(tokens.get(index+1).equals("=")){
                valueCheck(tokens.get(index+2));
            } else {
                throw new Exception("[ERROR]: Operator '=' is missing.");
            }
        } else {
            throw new Exception("[ERROR]: AttributeName is invalid.");
        }
    }

    public static void conditionCheck(ArrayList<String> tokens, int index) throws Exception{
        int bound = tokens.size() - index;
        if(bound == 5){
            if(tokens.get(index).equals("(")){
                if(tokens.get(index+4).equals(")")){
                    condition1(tokens, index+1);
                } else {
                    throw new Exception("[ERROR]: ')' is missing.");
                }
            } else {
                throw new Exception("[ERROR]: '(' is missing.");
            }
        } else if(bound == 3){
            condition1(tokens, index);
        } else if(bound == 7){
            condition1(tokens, index);
            index = index + 3;
            if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                index = index+1;
                condition1(tokens, index);
            } else {
                throw new Exception("[ERROR]: BoolOperator is invalid.");
            }
        } else if(bound == 13){
            if(tokens.get(index).equals("(")){
                if(tokens.get(index+12).equals(")")){
                    if(tokens.get(index+1).equals("(")){
                        if(tokens.get(index+5).equals(")")){
                            condition1(tokens, index+2);
                        } else {
                            throw new Exception("[ERROR]: ')' is missing.");
                        }
                        index = index+6;
                        if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                            if(tokens.get(index+1).equals("(")){
                                if(tokens.get(index+5).equals(")")){
                                    index = index+1;
                                    condition1(tokens, index);
                                } else {
                                    throw new Exception("[ERROR]: ')' is missing.");
                                }
                            } else {
                                throw new Exception("[ERROR]: '(' is missing.");
                            }
                        } else {
                            throw new Exception("[ERROR]: BoolOperator is invalid.");
                        }
                    } else {
                        throw new Exception("[ERROR]: '(' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: ')' is missing.");
                }
            } else {
                throw new Exception("[ERROR]: '(' is missing.");
            }
        } else if(bound == 11){
            if(tokens.get(index+1).equals("(")){
                if(tokens.get(index+10).equals(")")){
                    if(tokens.get(index).equals("(")){
                        if(tokens.get(index+5).equals(")")){
                            condition1(tokens, index+2);
                        } else {
                            throw new Exception("[ERROR]: ')' is missing.");
                        }
                        index = index+6;
                        if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                           condition1(tokens, index+1);
                        } else {
                            throw new Exception("[ERROR]: BoolOperator is invalid.");
                        }
                    } else if(tokens.get(index+5).equals("(")){
                        if(tokens.get(index+9).equals(")")){
                            condition1(tokens, index+1);
                            if(tokens.get(index+4).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                                condition1(tokens, index+6);
                            } else {
                                throw new Exception("[ERROR]: BoolOperator is invalid.");
                            }
                        } else {
                            throw new Exception("[ERROR]: ')' is missing.");
                        }
                    } else {
                        throw new Exception("[ERROR]: '(' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: ')' is missing.");
                }
            } else if(tokens.get(index).equals("(")){
                    if(tokens.get(index+10).equals(")")){
                        if(tokens.get(index+1).equals("(")){
                            if(tokens.get(index+5).equals(")")){
                                condition1(tokens, index+2);
                            } else {
                                throw new Exception("[ERROR]: ')' is missing.");
                            }
                            index = index+6;
                            if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                                if(tokens.get(index+1).equals("(")){
                                    if(tokens.get(index+5).equals(")")){
                                        index = index+1;
                                        condition1(tokens, index);
                                    } else {
                                        throw new Exception("[ERROR]: ')' is missing.");
                                    }
                                } else {
                                    throw new Exception("[ERROR]: '(' is missing.");
                                }
                            } else {
                                throw new Exception("[ERROR]: BoolOperator is invalid.");
                            }
                        } else {
                            throw new Exception("[ERROR]: '(' is missing.");
                        }
                    } else {
                        throw new Exception("[ERROR]: ')' is missing.");
                    }
            } else{
                throw new Exception("[ERROR]: '(' is missing.");
            }
        } else if(bound == 9){
            if(tokens.get(index).equals("(")){
                if(tokens.get(index+8).equals(")")){
                    condition1(tokens, index+1);
                    index = index + 4;
                    if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                        condition1(tokens, index+1);
                    } else {
                        throw new Exception("[ERROR]: BoolOperator is invalid.");
                    }
                } else if(tokens.get(index+4).equals(")")){
                        condition1(tokens, index+1);
                        index = index + 5;
                        if(tokens.get(index).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")){
                            condition1(tokens, index+1);
                        } else {
                            throw new Exception("[ERROR]: BoolOperator is invalid.");
                        }
                } else {
                    throw new Exception("[ERROR]: ')' is missing.");
                }
            } else if(tokens.get(index+3).equalsIgnoreCase("AND") || tokens.get(index).equalsIgnoreCase("OR")) {
                condition1(tokens, index);
                if(tokens.get(index+4).equals("(")){
                    if(tokens.get(index+8).equals(")")){
                        condition1(tokens, index+5);
                    } else {
                        throw new Exception("[ERROR]: ')' is missing.");
                    }
                } else {
                    throw new Exception("[ERROR]: '(' is missing.");
                }
            } else {
                throw new Exception("[ERROR]: BoolOperator is invalid.");
            }
        } else {
            throw new Exception("[ERROR]: Syntax is invalid.");
        }
    }
   public static void condition1(ArrayList<String> tokens, int index) throws Exception {
        if(!PlainTextCheck(tokens.get(index))){
            throw new Exception("[ERROR]: AttributeName is invalid.");
        }
        String comparator = tokens.get(index+1);
        comparator = comparator.toUpperCase();
        String[] comparators = {"==", ">", "<", ">=", "<=", "!=", " LIKE "};
        boolean isComparator = false;
        for (String operator : comparators) {
            if (comparator.equals(operator)) {
                isComparator = true;
                break;
            }
        }
        if(isComparator){
            valueCheck(tokens.get(index+2));
        } else {
            throw new Exception("[ERROR]: Comparator is missing.");
        }
    }
    public static int attribListCheck(ArrayList<String> tokens, int index) throws Exception {
        String attributeName = tokens.get(index+1);
        if(!tokens.get(index+2).equals(",")){
            if(PlainTextCheck(attributeName)){
                return index+1;
            } else {
                throw new Exception("[ERROR]: AttributeName is invalid.");
            }
        }
        if(PlainTextCheck(attributeName)){
            index = index + 2;
            attribListCheck(tokens, index);
        } else {
            throw new Exception("[ERROR]: AttributeName is invalid.");
        }
        return index+1;
    }
    public static void valueListCheck(ArrayList<String> tokens, int index) throws Exception {
        if(!tokens.get(index+2).equals(",")){
            valueCheck(tokens.get(index+1));
            return;
        }
        valueCheck(tokens.get(index+1));
        index = index + 2;
        valueListCheck(tokens, index);
    }

    public static void valueCheck(String value) throws Exception {
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

    public static boolean PlainTextCheck(String text){
        return text.matches("[a-zA-Z0-9]+");
    }
}
