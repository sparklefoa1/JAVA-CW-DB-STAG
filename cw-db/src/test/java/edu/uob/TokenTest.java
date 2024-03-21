package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TokenTest {
    private Token token;
    private ArrayList<String> tokens;
    @BeforeEach
    public void setup() {
        token = new Token();
        tokens = new ArrayList<>();
    }
    @Test
    public void testParser(){
        tokens = token.setup();
        for (String element : tokens) {
            System.out.println(element);
        }
    }
}
