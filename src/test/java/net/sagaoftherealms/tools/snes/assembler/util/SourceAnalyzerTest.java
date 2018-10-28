package net.sagaoftherealms.tools.snes.assembler.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class SourceAnalyzerTest {

    @Test
    public void testStructDefinesSizeOf() {
        fail("Struct should define a __sideof__.  See https://wla-dx.readthedocs.io/en/latest/asmdiv.html#struct-enemy-object");
    }
}
