package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.DirectiveArgumentsValidator;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.toScanner;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 *  x = a whole number
 *  f = a number with a decimal part
 *  c = a character
 *  s = a String value (expands to "some text"
 *  l = a label (which will be a string)
 *
 *  t = a boolean expression
 *  e = a integer expression
 *
 *  {x|y} One of x, y...
 *  []{} = A comma separated List of types in the braces (see .DB in https://wla-dx.readthedocs.io/en/latest/asmdiv.html)
 *
 *  ? = Optional
 *
 *  (pattern) a pattern
 *
 * Tests for a {@link net.sagaoftherealms.tools.snes.assembler.definition.directives.DirectiveArgumentsValidator}
 */
public class DirectiveArgumentsValidatorTester {

    @ParameterizedTest
    @CsvSource({"x, 5",
    "f,5.0",
    "c,'''a'''",
    "c,'''0'''"})
    public void validateNumbers(String pattern, String token) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
        assertTrue(validator.accept(new Token(token, TokenTypes.NUMBER)));
    }

    @ParameterizedTest
    @CsvSource({"x, 5.1",
            "x,'''a'''",
            "f,'''0'''",
            "c, 5"})
    public void validateNumbersFailures(String pattern, String token) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
        assertFalse(validator.accept(new Token(token, TokenTypes.NUMBER)));
    }

    @Test
    public void validateStrings() {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("s");
        assertTrue(validator.accept(new Token("This is a String", TokenTypes.STRING)));
    }

    @Test
    public void validateLabel() {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("l");
        assertTrue(validator.accept(new Token("aLabel", TokenTypes.LABEL)));
    }

    @ParameterizedTest
    @CsvSource({"4 'a', x c, 'NUMBER, NUMBER'",
                "4 \"This is a String\", x s, 'NUMBER, STRING'",
            "'4, \"This is a String\"', 'x, s', 'NUMBER, COMMA, STRING'",
                })
    public void validateMultipleArgumentsPattern(String sourceLine, String pattern,  String tokenTypes) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
        String[] typeArray = tokenTypes.split(",");
        var scanner = toScanner(sourceLine);
        for (String type : typeArray) {
            var token = scanner.getNextToken();
            assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
            assertTrue(validator.accept(token));
        }

    }

    @ParameterizedTest
    @CsvSource({"'[]{x}, c'",})
    public void checkPatternIsValid(String pattern) {
        assertThrows(IllegalArgumentException.class, ()->{
            new DirectiveArgumentsValidator(pattern);
        });
    }
    @ParameterizedTest
    @CsvSource({"'4, 4, 5', []{x}, 'NUMBER, COMMA, NUMBER, COMMA, NUMBER'",
                "'4, ''c'', 5', []{cx}, 'NUMBER, COMMA, NUMBER, COMMA, NUMBER'",
                "'4, 5 ''c''', []{x} c, 'NUMBER, COMMA, NUMBER, NUMBER'",

    })
    public void validateArray(String sourceLine, String pattern,  String tokenTypes) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
        String[] typeArray = tokenTypes.split(",");
        var scanner = toScanner(sourceLine);
        for (String type : typeArray) {
            var token = scanner.getNextToken();
            System.out.println(token.toString());
            assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
            assertTrue(validator.accept(token));
        }

    }

    @ParameterizedTest
    @CsvSource({"4 + 4, 'NUMBER, PLUS, NUMBER'",
            "8 * 4 + 2, 'NUMBER, MULTIPLY, NUMBER, PLUS, NUMBER'",})
    public void validateNumericExpression(String sourceLine, String tokenTypes) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("e");
        String[] typeArray = tokenTypes.split(",");
        var scanner = toScanner(sourceLine);
        for (String type : typeArray) {
            var token = scanner.getNextToken();
            assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
            assertTrue(validator.accept(token));
        }
    }

    @ParameterizedTest
    @CsvSource({"4 + 4 FLOUR, 'NUMBER, PLUS, NUMBER, LABEL'",
            "8 * 4 + 2 6+3, 'NUMBER, MULTIPLY, NUMBER, PLUS, NUMBER, NUMBER, PLUS, NUMBER'",})
    public void validateComplexArguments(String sourceLine, String tokenTypes) {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("{elx} {elx}");
        String[] typeArray = tokenTypes.split(",");
        var scanner = toScanner(sourceLine);
        for (String type : typeArray) {
            var token = scanner.getNextToken();
            System.out.println(type);
            assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
            assertTrue(validator.accept(token));
        }
    }



    @Test
    public void validateBooleanExpression() {
        DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("t");
        assertTrue(validator.accept(new Token("4", TokenTypes.NUMBER)));
        assertTrue(validator.accept(new Token(">", TokenTypes.GT)));
        assertTrue(validator.accept(new Token("4", TokenTypes.NUMBER)));
    }


}
