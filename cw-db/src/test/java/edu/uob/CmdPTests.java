package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CmdPTests {
    private CommandParser testCommandParser;
    private Database testDB = new Database("testDB");

    @Test
    public void testCheckSyntax() {
        try {
            //testCommandParser = new CommandParser(" USE testdb; ");
            //System.out.println(testCommandParser.checkSyntax());
            //System.out.println(DatabaseManager.getInstance().getCurrentDatabase().getTable("people").getColumns().get(0).getName());

            //testCommandParser = new CommandParser(" CREATE DATABASE mydb; ");
           //System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" use mydb; ");
            System.out.println(testCommandParser.checkSyntax());// 在这里设置了current database
            //testCommandParser = new CommandParser(" CREATE TABLE markS(name, mark, pass); ");
            //System.out.println(testCommandParser.checkSyntax());

            //testCommandParser = new CommandParser(" drop database testdb; ");
            //System.out.println(testCommandParser.checkSyntax());

            //testCommandParser = new CommandParser(" drop table marks; ");
            //System.out.println(testCommandParser.checkSyntax());

            /*testCommandParser = new CommandParser(" insert into marks values('Simon', 65, true); ");
            System.out.println(testCommandParser.checkSyntax());
            testCommandParser = new CommandParser(" insert into marks values('Sion', 55, true); ");
            System.out.println(testCommandParser.checkSyntax());
            testCommandParser = new CommandParser(" insert into marks values('Rob', 35, false); ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" insert into marks values('Chris', 20, false); ");
            System.out.println(testCommandParser.checkSyntax());*/

            //testCommandParser = new CommandParser(" select pass, id from marks; ");
            //System.out.println(testCommandParser.checkSyntax());

            //testCommandParser = new CommandParser(" select name, id from marks where pass == false and id > 3; ");
            //System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" UPDATE marks SET mark = 38 WHERE name == 'Chris' ; ");
            System.out.println(testCommandParser.checkSyntax());

            /*testCommandParser = new CommandParser(" DELETE FROM marks WHERE mark < 40; ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" SELECT name FROM marks WHERE mark > 60; ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" Drop     TABLE myTable ; ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" JOIN coursework AND marks ON submission AND id; ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" ALTER     TABLE myTable DROP ages ; ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" INSERT INTO myTable VALUES ( 'chris', 65, 42 ); ");
            System.out.println(testCommandParser.checkSyntax());

            testCommandParser = new CommandParser(" SELECT * FROM marks WHERE name != 'Sion'; ");
            System.out.println(testCommandParser.checkSyntax());*/
        } catch (IOException e) {
            System.err.println("ioe error");
        }
    }
}
