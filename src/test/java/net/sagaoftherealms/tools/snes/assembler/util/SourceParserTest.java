package net.sagaoftherealms.tools.snes.assembler.util;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class SourceParserTest {

    @Test
    public void testAnonymousLabelNode() {
        fail("This test will test that when - or + are used as labels the parser identifies them as such instead of as parts of a arthimetic operation");
    }

    @Test
    public void testShiftVsGetByteNode() {
        fail("This test will test >,<, >>,<< are handled as get byte and bit shift nodes");
    }


}
