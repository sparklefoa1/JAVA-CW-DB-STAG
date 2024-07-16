package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CmdPTests {
    private CommandParser commandParser;
    private String command;

    @BeforeEach
    public void setup() {
        command = " CREATE     TABLE myTable ( id , INT , name ; ";
        commandParser = new CommandParser(command);
    }

    @Test
    public void testCheckSyntax() {
        System.out.println(commandParser.checkSyntax());
    }
}
