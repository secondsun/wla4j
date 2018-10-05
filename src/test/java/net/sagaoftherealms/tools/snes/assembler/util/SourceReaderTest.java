package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.token.TokenTypes;
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
        assertEquals(TokenTypes.STRING, token.getType());
        assertEquals("This is a String Token", token.getString());
    }

    @Test
    public void firstStringTokenWithExpandedMacro() {
        fail("See pass_1.c#649");
    }


    @Test
    public void testNextToken() {
        fail();
    }

    @Test
    public void testParseDirectiveToken() {
        fail();//see pass_1.c#780
    }

    @Test
    public void testParseRamsectionToken() {
        fail();//see pass_1.c#776
    }

    @Test
    public void testParseEnumToken() {
        //see pass_1.c#776
        fail();
    }

    @Test
    public void testBasicLabel() {
        fail("See pass_1.c#783");
    }

    @Test
    public void testLabelFailsIfOutputLibrary() {
        fail("See pass_1.c#788");
    }

    @Test
    public void testLabelFailsIfNoMemoryPosition() {
        fail("See pass_1.c#792");
    }

    @Test
    public void testLabelFailsIfInBankHeaderSection() {
        fail("See pass_1.c#802");
    }


    @Test
    public void testLabelInActiveMacro() {
        fail("See pass_1.c#807");
    }


    @Test
    public void testOpcodeToken() {
        fail("See pass_1.c#819");
    }

    @Test
    public void testDecode65816OpcodeToken() {
        fail("See decode_65816.c");
    }

    @Test
    public void testDecodeOtherArchOpcodeToken() {
        fail("Look, you're going to have to take apart the switch statements starting at pass_1.c#863.  There are many");
    }


}
