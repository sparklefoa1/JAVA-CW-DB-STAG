package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TokenTest {
    private Token token;
    private ArrayList<String> tokens;
    private String query = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ; ";
    private  String alter = "ALTER  TABLE people  ADD  header ;";
    @BeforeEach
    public void setup() {
        token = new Token();
        tokens = new ArrayList<>();
    }
    @Test
    public void testParser() throws Exception {
        tokens = token.setup(alter);
        SyntaxCheck.command(tokens);
        for (String element : tokens) {
            System.out.println(element);
        }
    }
}
