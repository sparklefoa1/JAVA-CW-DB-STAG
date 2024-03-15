package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParserTest {
    private Parser parser;
    @BeforeEach
    public void setup() {parser = new Parser();}
    @Test
    public void testParser(){parser.setup();}
}
