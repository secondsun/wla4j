package dev.secondsun.wla4j.assembler.util;

import static dev.secondsun.wla4j.assembler.util.TestUtils.toScanner;
import static org.junit.jupiter.api.Assertions.*;

import dev.secondsun.wla4j.assembler.definition.directives.DirectiveArgumentsValidator;
import dev.secondsun.wla4j.assembler.definition.opcodes.OpCodeZ80;
import dev.secondsun.wla4j.assembler.pass.parse.LabelDefinitionNode;
import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token.Position;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * x = a whole number f = a number with a decimal part c = a character s = a String value (expands
 * to "some text" l = a label (which will be a string)
 *
 * <p>t = a boolean expression e = a integer expression
 *
 * <p>{x|y} One of x, y... []{} = A comma separated List of types in the braces (see .DB in
 * https://wla-dx.readthedocs.io/en/latest/asmdiv.html)
 *
 * <p>? = Optional
 *
 * <p>(pattern) a pattern
 *
 * <p>Tests for a {@link DirectiveArgumentsValidator}
 */
public class DirectiveArgumentsValidatorTester {

  private final SourceScanner mockerScanner =
      new SourceScanner(null, OpCodeZ80.opcodes()) {
        @Override
        public Token getNextToken() {
          return new Token("\n", TokenTypes.END_OF_INPUT, "", new Position(0, 0, 0, 0));
        }
      };
  private final SourceParser parser =
      new SourceParser(mockerScanner) {
        @Override
        public Node nextNode() {
          return new LabelDefinitionNode("Const", null);
        }
      };

  @ParameterizedTest
  @CsvSource({"x, 5", "f,5.0", "c,'''a'''", "c,'''0'''"})
  public void validateNumbers(String pattern, String token) {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
    assertTrue(
        validator
            .accept(new Token(token, TokenTypes.NUMBER, "", new Position(0, 0, 0, 0)), parser)
            .isPresent());
  }

  @ParameterizedTest
  @CsvSource({"x, 5.1", "x,'''a'''", "f,'''0'''", "c, 5"})
  public void validateNumbersFailures(String pattern, String token) {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
    assertFalse(
        validator
            .accept(new Token(token, TokenTypes.NUMBER, "", new Position(0, 0, 0, 0)), parser)
            .isPresent());
  }

  @Test
  public void validateStrings() {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("s");
    assertTrue(
        validator
            .accept(
                new Token("This is a String", TokenTypes.STRING, "", new Position(0, 0, 0, 0)),
                parser)
            .isPresent());
  }

  @Test
  public void validateLabel() {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator("l");
    assertTrue(
        validator
            .accept(new Token("aLabel", TokenTypes.LABEL, "", new Position(0, 0, 0, 0)), parser)
            .isPresent());
  }

  @ParameterizedTest
  @CsvSource({
    "4 'a', x c, 'NUMBER, NUMBER'",
    "4 \"This is a String\", x s, 'NUMBER, STRING'",
    "'4, \"This is a String\"', 'x, s', 'NUMBER, COMMA, STRING'",
  })
  public void validateMultipleArgumentsPattern(
      String sourceLine, String pattern, String tokenTypes) {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
    String[] typeArray = tokenTypes.split(",");
    var scanner = toScanner(sourceLine);
    for (String type : typeArray) {
      var token = scanner.getNextToken();
      assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
      assertTrue(validator.accept(token, parser).isPresent());
    }
  }

  @ParameterizedTest
  @CsvSource({
    "'[]{x}, c'",
  })
  public void checkPatternIsValid(String pattern) {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new DirectiveArgumentsValidator(pattern);
        });
  }

  @ParameterizedTest
  @CsvSource({
    "'4, 4, 5', []{x}, 'NUMBER, COMMA, NUMBER, COMMA, NUMBER'",
    "'4, ''c'', 5', []{cx}, 'NUMBER, COMMA, NUMBER, COMMA, NUMBER'",
    "'4, 5 ''c''', []{x} c, 'NUMBER, COMMA, NUMBER, NUMBER'",
  })
  public void validateArray(String sourceLine, String pattern, String tokenTypes) {
    DirectiveArgumentsValidator validator = new DirectiveArgumentsValidator(pattern);
    String[] typeArray = tokenTypes.split(",");
    var scanner = toScanner(sourceLine);
    for (String type : typeArray) {
      var token = scanner.getNextToken();

      assertEquals(TokenTypes.valueOf(type.trim()), token.getType());
      assertTrue(validator.accept(token, parser).isPresent());
    }
  }
}
