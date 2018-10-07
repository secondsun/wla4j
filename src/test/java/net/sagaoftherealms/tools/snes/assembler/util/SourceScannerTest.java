package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.token.TokenTypes;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SourceScannerTest {

    @ParameterizedTest
    @CsvSource({"\"This is a String Token\"",
                "\"This is another String Token\""})
    public void firstStringToken(String sourceLine) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile,lineNumber);

        var scanner = data.startRead();

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.STRING, token.getType());
        //The string of the token should be sourceLine minue quotes
        assertEquals(sourceLine.replace('"',' ').trim(), token.getString());
    }

    @Test()
    public void unterminatedStringThrowsException() {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($("\"This should crash"), inputFile,lineNumber);

        var scanner = data.startRead();

        Assertions.assertThrows(IllegalStateException.class, ()->scanner.getNextToken());

        
    }
    
    @Test
    public void firstStringTokenWithExpandedMacro() {
        fail("See pass_1.c#649");
    }


    /**
     * This test tests single directive tokens and makes sure that we can match them.
     * 
     * Validation directives is another test.
     * 
     * @param sourceLine the source code line
     * @param expectedDirective the expected directive sourceLine parses to.
     */
    @ParameterizedTest
    @CsvSource({".IF, IF"})
    public void testSimpleParseDirectiveToken(String sourceLine, String expectedDirective) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile,lineNumber);
        
        var scanner = data.startRead();

        var token = scanner.getNextToken();
        
        assertEquals(TokenTypes.DIRECTIVE, token.getType());
        assertEquals("." + expectedDirective, token.getString());
        
        
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

    private static InputStream $(String inputString) {
        return IOUtils.toInputStream(inputString, Charset.defaultCharset());
    }
    
    
}
