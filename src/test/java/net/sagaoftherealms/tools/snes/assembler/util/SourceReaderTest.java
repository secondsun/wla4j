package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SourceReaderTest {

    @Test
    public void firstStringToken() {
        var data = new InputData(new Flags(" test.out "));
        data.includeFile(SourceReaderTest.class.getClassLoader().getResourceAsStream("tokenTests/string.s"), "string.s",0);
        var reader = data.startRead();

        var token = reader.getNextToken();
        assertEquals(TokenTypes.String, token.getType());

        fail();
    }

    @Test
    public void testNextToken() {
        fail();
    }

}
