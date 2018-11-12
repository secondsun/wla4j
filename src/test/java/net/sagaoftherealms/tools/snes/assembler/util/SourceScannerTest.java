package net.sagaoftherealms.tools.snes.assembler.util;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.stream.Stream;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCodeSpc700;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.main.Test65816IncludeData;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class SourceScannerTest {

    public static Stream<Arguments> opcodeGenerator65816() {
        return Arrays.stream(Opcodes65816.opt_table).map(opcode -> {
            var code = opcode.getOp().split(" ")[0].split("\\.")[0];
            var sourceLine = opcode.getOp();
            sourceLine = sourceLine.replace("x", "0ah");
            sourceLine = sourceLine.replace("?", "0ah");
            sourceLine = sourceLine.replace("&", "0ah");
            return Arguments.of(sourceLine, code);
        });
    }

    public static Stream<Arguments> opcodeGeneratorSPC700() {
        return Arrays.stream(OpCodeSpc700.OPCODES).map(opcode -> {
            var code = opcode.getOp().split(" ")[0].split("\\.")[0];
            var sourceLine = opcode.getOp();
            sourceLine = sourceLine.replace("x", "0ah");
            sourceLine = sourceLine.replace("?", "0ah");
            sourceLine = sourceLine.replace("&", "0ah");
            return Arguments.of(sourceLine, code);
        });
    }

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
    public void testMultiplyToken() {
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
            "$100.w, 256",//Hex with size
            "'''x''', 120", //Char
            "'''0''', 48", //Char
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

    /**
     * This test tests single directive tokens and makes sure that we can match them.
     * <p>
     * Validation directives is another test.
     *
     * @param sourceLine the source code line
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

    @ParameterizedTest
    @CsvSource({"10.b, NUMBER, '10', .b",
            "'$20.w', NUMBER, '$20', .w",
            "test.l, LABEL, test, .l, ",
    })
    public void testValueSizeTypeToken(String sourceLine, String valueTokenType,
            String valueTokenString, String expectedValueSize) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var valueToken = scanner.getNextToken();
        var typeToken = scanner.getNextToken();

        assertEquals(TokenTypes.valueOf(valueTokenType), valueToken.getType());
        assertEquals(valueTokenString, valueToken.getString());
        assertEquals(TokenTypes.SIZE, typeToken.getType());
        assertEquals(expectedValueSize, typeToken.getString());

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

    @ParameterizedTest
    @CsvSource({
            "label, label", //basic label, no :
            "label2:, label2", //basic label with colon
            "_label, label",
            //underscore label IE local label (see https://wla-dx.readthedocs.io/en/latest/asmsyntax.html#labels)
            "@label.b, label", //Child label
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
        assertEquals(labelName, TokenUtil.getLabelName(token));
    }

    @Test
    public void testEndOfLineToken() {
        var sourceLine = "Label1:\nLabel2";
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);
        scanner.getNextToken();

        var token = scanner.getNextToken();
        assertEquals(TokenTypes.EOL, token.getType());
    }

    @ParameterizedTest
    @MethodSource({"opcodeGenerator65816"})
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

    @ParameterizedTest
    @MethodSource({"opcodeGeneratorSPC700"})
    public void testOpcodeSPc700(String sourceLine, String opCode) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(OpCodeSpc700.OPCODES);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.OPCODE, token.getType());
        assertEquals(opCode, token.getString());

    }

    @Test
    public void testCanScanWholeFileAndNotCrash() {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"),
                "main.s", 0);
        data.includeFile(
                Test65816IncludeData.class.getClassLoader().getResourceAsStream("defines.i"),
                "defines.i", 1);
        data.includeFile(
                Test65816IncludeData.class.getClassLoader().getResourceAsStream("snes_memory.i"),
                "snes_memeory.i", 2);
        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();
        while (token != null) {
            System.out.println(token);
            if (scanner.endOfInput()) {
                break;
            }
            token = scanner.getNextToken();
        }

    }

    @Test
    public void testCanScanFerris() {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(
                Test65816IncludeData.class.getClassLoader().getResourceAsStream("ferris-kefren.s"),
                "ferris-kefren.s", 0);

        var scanner = data.startRead(Opcodes65816.opt_table);

        var token = scanner.getNextToken();
        while (token != null) {
            System.out.println(token);
            if (scanner.endOfInput()) {
                break;
            }
            token = scanner.getNextToken();
        }

    }

    @Test
    public void testAllDirectives() {
        Arrays.stream(AllDirectives.values()).forEach(it -> {
            var sourceLine = AllDirectives.generateDirectiveLine(it.getPattern(), true);
            var data = new InputData(new Flags("main.s"));
            data.includeFile($(sourceLine), "main.s", 0);

            var scanner = data.startRead(OpCodeSpc700.OPCODES);
            System.out.println(sourceLine);
            while (!scanner.endOfInput()) {
                System.out.print(scanner.getNextToken());
                System.out.print(" ");
            }
            System.out.println(" ");
        });

    }

    @ParameterizedTest
    @CsvSource({"!, NOT, ''",
            "<=, LT, EQUAL",
            ">=, GT, EQUAL",
            "==, EQUAL, EQUAL",
            "\\2, ESCAPE, NUMBER",
            "\\!, ESCAPE, NOT",
            "\\@, ESCAPE, AT",
    })
    public void scanIfAndMacroOperators(String sourceLine, String operator1, String operator2) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile($(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(OpCodeSpc700.OPCODES);

        var token = scanner.getNextToken();

        assertEquals(TokenTypes.valueOf(operator1), token.getType());
        if (!Strings.isNullOrEmpty(operator2)) {
            token = scanner.getNextToken();
            assertEquals(TokenTypes.valueOf(operator2), token.getType());
        }


    }

}
