package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.opcodes.defines.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.token.TokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
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
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.STRING, token.getType());
        //The string of the token should be sourceLine minue quotes
        assertEquals(sourceLine.replace('"', ' ').trim(), token.getString());
    }

    @ParameterizedTest
    @CsvSource({
            "',', COMMA",
            "|, OR",
            "&, AND",
            "^, POWER",
            "+, PLUS",
            "-, MINUS",
            "'#', MODULO",
            "~, XOR",
            "'/ ', DIVIDE",
            "<, LT",
            ">, GT",
            "[, LEFT_BRACKET",
            "], RIGHT_BRACKET",
            "(, LEFT_PAREN",
            "), RIGHT_PAREN"})
    public void testOperators(String sourceLine, String tokenType) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.valueOf(tokenType), token.getType());
        assertEquals(sourceLine.trim(), token.getString());
    }

    //Multiply gets a special test because it begins a comment
    @Test
    public void testMultiplyToken()
    {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($("42 *"), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);
        scanner.getNextToken(); //Skip 42
        var token = scanner.getNextToken();

        assertEquals(TokenTypes.MULTIPLY, token.getType());
        assertEquals("*", token.getString());
    }

    @Test()
    public void unterminatedStringThrowsException() {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($("\"This should crash"), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        Assertions.assertThrows(IllegalStateException.class, () -> scanner.getNextToken());
    }

    @ParameterizedTest
    @CsvSource({"0, 0",//dec
            "1, 1",//dec
            "0.0, 0.0",//dec
            "0.1, 0.1",//dec
            "0ah, 10",//Hex
            "$100, 256",//Hex
            "'''x''', 120", //Char
            "%0101, 5"//binary
    })
    public void numberTokens(String sourceLine, double value) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.NUMBER, token.getType());
        assertEquals(sourceLine, token.getString());
        assertEquals(value, TokenUtil.getDouble(token));
        assertEquals((int) value, TokenUtil.getInt(token));
    }

    //test that ah is treated as label and not a number
    @Test
    public void labelVsHexNumber() {

        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($("ah"), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.LABEL, token.getType());

    }

    @Test
    public void firstStringTokenWithExpandedMacro() {
        fail("See pass_1.c#649");
    }


    /**
     * This test tests single directive tokens and makes sure that we can match them.
     * <p>
     * Validation directives is another test.
     *
     * @param sourceLine        the source code line
     * @param expectedDirective the expected directive sourceLine parses to.
     */
    @ParameterizedTest
    @CsvSource({".IF, IF",
            ".ELSE, ELSE,",
            ".8BIT, 8BIT,",
            ".ELSEIF, ELSEIF",
    })
    public void testSimpleParseDirectiveToken(String sourceLine, String expectedDirective) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.DIRECTIVE, token.getType());
        assertEquals("." + expectedDirective, token.getString());

    }



    @Test()
    public void emptyDirectiveThrowsException() {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(". Crash"), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        Assertions.assertThrows(IllegalStateException.class, () -> scanner.getNextToken());
    }


    @Test
    public void testParseRamSectionToken() {
        fail();//see pass_1.c#776
    }

    @Test
    public void testParseEnumToken() {
        //see pass_1.c#776
        fail();
    }

    @ParameterizedTest
    @CsvSource({
            "label, label", //basic label, no :
            "label2:, label2", //basic label with colon
            "_label, label", //underscore label IE local label (see https://wla-dx.readthedocs.io/en/latest/asmsyntax.html#labels)
            "@label, label", //Child label
            "@@@@label, label",//Deeply nested child label
    })
    public void testBasicLabel(String sourceLine, String labelName) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.LABEL, token.getType());
        assertEquals(sourceLine, token.getString());
        assertEquals(labelName, TokenUtil.getLabelName(token));
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

    @ParameterizedTest
    @MethodSource({"opcodeGenerator"})
    public void testOpcode(String sourceLine, String opCode) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.OPCODE, token.getType());
        assertEquals(opCode, token.getString());

    }

    public static Stream<Arguments> opcodeGenerator() {
        return Arrays.stream(Opcodes65816.opt_table).map(opcode -> {
            var code = opcode.getOp().split(" ")[0];
            var sourceLine = opcode.getOp();
            sourceLine = sourceLine.replace("x", "0ah");
            sourceLine = sourceLine.replace("?", "0ah");
            sourceLine = sourceLine.replace("&", "0ah");
            return Arguments.of(sourceLine, code);
        });
    }

}
