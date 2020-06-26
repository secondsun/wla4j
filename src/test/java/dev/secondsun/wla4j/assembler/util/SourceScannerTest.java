package dev.secondsun.wla4j.assembler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.definition.opcodes.OpCode65816;
import dev.secondsun.wla4j.assembler.definition.opcodes.OpCodeSpc700;
import dev.secondsun.wla4j.assembler.definition.opcodes.OpCodeZ80;
import dev.secondsun.wla4j.assembler.main.InputData;
import dev.secondsun.wla4j.assembler.pass.parse.ErrorNode;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class SourceScannerTest {

  private static final Random random = new Random();

  public static Stream<Arguments> opcodeGenerator65816() {
    return Arrays.stream(OpCode65816.opcodes())
        .map(
            opcode -> {
              var code = opcode.getOp().split(" ")[0];
              var sourceLine = opcode.getOp();
              sourceLine = sourceLine.replace("x", "0ah");
              sourceLine = sourceLine.replace("?", "0ah");
              sourceLine = sourceLine.replace("&", "0ah");
              return Arguments.of(sourceLine, code);
            });
  }

  public static Stream<Arguments> opcodeGeneratorSPC700() {
    return Arrays.stream(OpCodeSpc700.opcodes())
        .map(
            opcode -> {
              var code = opcode.getOp().split(" ")[0].split("\\.")[0];
              var sourceLine = opcode.getOp();
              sourceLine = sourceLine.replace("x", "0ah");
              sourceLine = sourceLine.replace("?", "0ah");
              sourceLine = sourceLine.replace("&", "0ah");
              return Arguments.of(sourceLine, code);
            });
  }

  @Test
  public void testScanErrorHandling() {

    var source = "."; // If true  {errorNode} end;

    var parser = TestUtils.asParser(source);
    ErrorNode ifNode = (ErrorNode) parser.nextNode();
    Assertions.assertEquals(
        TokenTypes.ERROR, ((ParseException) ifNode.getException()).getProblemToken().getType());
  }

  @ParameterizedTest
  @CsvSource({"\"This is a String Token\"", "\"This is another String Token\""})
  public void firstStringToken(String sourceLine) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.STRING, token.getType());
    // The string of the token should be sourceLine minue quotes
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
    "), RIGHT_PAREN"
  })
  public void testOperators(String sourceLine, String tokenType) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.valueOf(tokenType), token.getType());
    assertEquals(sourceLine.trim(), token.getString());
  }

  @Test
  public void simpleScan() {
    var line = "\t    Bullet_Aim\n";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(line), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();
    assertEquals("Bullet_Aim", token.getString());
  }

  // Multiply gets a special test because it begins a comment
  @Test
  public void testMultiplyToken() {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream("42 *"), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());
    scanner.getNextToken(); // Skip 42
    var token = scanner.getNextToken();

    assertEquals(TokenTypes.MULTIPLY, token.getType());
    assertEquals("*", token.getString());
  }

  @Test()
  public void unterminatedStringThrowsException() {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream("\"This \nshould\n crash"), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    assertEquals(TokenTypes.ERROR, scanner.getNextToken().getType());
  }

  @ParameterizedTest
  @CsvSource({
    "0, 0", // dec
    "1, 1", // dec
    "0.0, 0.0", // dec
    "0.1, 0.1", // dec
    "0ah, 10", // Hex
    "$100, 256", // Hex
    "$100.w, 256", // Hex with size
    "'''x''', 120", // Char
    "'''0''', 48", // Char
    "%0101, 5" // binary
  })
  public void numberTokens(String sourceLine, double value) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.NUMBER, token.getType());

    assertEquals(value, TokenUtil.getDouble(token));
    assertEquals((int) value, TokenUtil.getInt(token));
  }

  // test that ah is treated as label and not a number
  @Test
  public void labelVsHexNumber() {

    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream("ah"), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.LABEL, token.getType());
  }

  /**
   * This test tests single directive tokens and makes sure that we can consume them.
   *
   * <p>Validation directives is another test.
   *
   * @param sourceLine the source code line
   * @param expectedDirective the expected directive sourceLine parses to.
   */
  @ParameterizedTest
  @CsvSource({
    ".IF, IF",
    ".ELSE, ELSE,",
    ".8BIT, 8BIT,",
    ".ELSEIF, ELSEIF",
  })
  public void testSimpleParseDirectiveToken(String sourceLine, String expectedDirective) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.DIRECTIVE, token.getType());
    assertEquals("." + expectedDirective, token.getString());
  }

  @ParameterizedTest
  @CsvSource({
    "10.b, NUMBER, '10', .b",
    "'$20.w', NUMBER, '$20', .w",
    "test.l, LABEL, test, .l, ",
  })
  public void testValueSizeTypeToken(
      String sourceLine, String valueTokenType, String valueTokenString, String expectedValueSize) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var valueToken = scanner.getNextToken();
    var typeToken = scanner.getNextToken();

    assertEquals(TokenTypes.valueOf(valueTokenType), valueToken.getType());
    assertEquals(valueTokenString, valueToken.getString());
    assertEquals(TokenTypes.SIZE, typeToken.getType());
    assertEquals(expectedValueSize, typeToken.getString());
  }

  @ParameterizedTest
  @CsvSource({
    "singleLine: ;a great label, LABEL",
    ".endIf;a great label, DIRECTIVE",
    "'multiLine: /* This is a \n multiline comment*/', LABEL",
    "'multiLine:\n* This is a commentedLine as well.', LABEL"
  })
  public void sourceKeepsComments(String source, String tokenType) {
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(source), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token1 = scanner.getNextToken(true, false);
    var token2 = scanner.getNextToken(true, false);
    if (token2.getType().equals(TokenTypes.EOL)) {
      token2 = scanner.getNextToken(true, false);
    }

    assertEquals(TokenTypes.valueOf(tokenType), token1.getType());
    assertEquals(TokenTypes.COMMENT, token2.getType());
  }

  @ParameterizedTest
  @CsvSource({"'multiLine: /* This is a \n multiline comment*/', 1,0,1,10,1,11,2,20 "})
  public void scannerIncludesLinePositionInformation(
      String source,
      int beginLine1,
      int beginPosition1,
      int endLine1,
      int endPosition1,
      int beginLine2,
      int beginPosition2,
      int endLine2,
      int endPosition2) {

    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(source), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token1 = scanner.getNextToken(true, false);
    var token2 = scanner.getNextToken(true, false);

    assertEquals(TokenTypes.LABEL, token1.getType());
    assertEquals(beginLine1, token1.getPosition().beginLine);
    assertEquals(endLine1, token1.getPosition().getEndLine());

    assertEquals(beginPosition1, token1.getPosition().beginOffset);
    assertEquals(endPosition1, token1.getPosition().getEndOffset());

    assertEquals(TokenTypes.COMMENT, token2.getType());
    assertEquals("/* This is a \n multiline comment*/", token2.getString());
    assertEquals(beginLine2, token2.getPosition().beginLine);
    assertEquals(endLine2, token2.getPosition().getEndLine());

    assertEquals(beginPosition2, token2.getPosition().beginOffset);
    assertEquals(endPosition2, token2.getPosition().getEndOffset());
  }

  @Test()
  public void emptyDirectiveThrowsException() {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(". Crash"), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    assertEquals(TokenTypes.ERROR, scanner.getNextToken().getType());
  }

  @ParameterizedTest
  @CsvSource({
    "label, label", // basic label, no :
    "label2:, label2", // basic label with colon
    "\\2:, \\2", // basic label with colon
    ":label2, label2", // basic label with colon at beginning
    ":\\2Test, \\2Test", // a macro label with a bank and a template
    "_label, _label",
    // underscore label IE local label (see
    // https://wla-dx.readthedocs.io/en/latest/asmsyntax.html#labels)
    "@label.b, @label", // Child label
    "label.namespace, label.namespace", // Namespaced Label
    "@@@@label, @@@@label", // Deeply nested child label
    "---:, '---'", // unnamed label
    "+++, '+++'", // unnamed label
    "3BytePointer,3BytePointer",
    "100TH_RING, 100TH_RING",
    "NUM_SEED_TREES*8, 'NUM_SEED_TREES'" // label during a
  })
  public void testBasicLabel(String sourceLine, String labelName) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.LABEL, token.getType());
    assertEquals(labelName, TokenUtil.getLabelName(token));
  }

  @Test
  public void testELabelIsNotOpcode() {
    var scanner = TestUtils.toScanner("e", OpCodeZ80.opcodes());
    var token = scanner.getNextToken();
    assertEquals(TokenTypes.LABEL, token.getType());
  }

  @Test
  public void testEndOfLineToken() {
    var sourceLine = "Label1:\nLabel2";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());
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

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

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

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeSpc700.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.OPCODE, token.getType());
    assertEquals(opCode, token.getString());
  }

  @Test
  public void testCanScanWholeFileAndNotCrash() {
    InputData data = new InputData();
    data.includeFile(
        SourceScannerTest.class.getClassLoader().getResourceAsStream("main.s"), "main.s", 0);
    data.includeFile(
        SourceScannerTest.class.getClassLoader().getResourceAsStream("defines.i"), "defines.i", 1);
    data.includeFile(
        SourceScannerTest.class.getClassLoader().getResourceAsStream("snes_memory.i"),
        "snes_memeory.i",
        2);
    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();
    while (token != null) {

      if (scanner.endOfInput()) {
        break;
      }
      token = scanner.getNextToken();
    }
  }

  @Test
  public void testCanScanFerris() {
    InputData data = new InputData();
    data.includeFile(
        SourceScannerTest.class.getClassLoader().getResourceAsStream("ferris-kefren.s"),
        "ferris-kefren.s",
        0);

    var scanner = data.startRead(OpCode65816.opcodes());

    var token = scanner.getNextToken();
    while (token != null) {

      if (scanner.endOfInput()) {
        break;
      }
      token = scanner.getNextToken();
    }
  }

  @Test
  public void testAllDirectives() {
    Arrays.stream(AllDirectives.values())
        .forEach(
            directive -> {
              var sourceLine = generateDirectiveLine(directive.getPattern(), true);
              var data = new InputData();
              data.includeFile(TestUtils.toStream(sourceLine), "main.s", 0);

              var scanner = data.startRead(OpCodeSpc700.opcodes());

              while (!scanner.endOfInput()) {
                scanner.getNextToken();
              }
            });
  }

  @ParameterizedTest
  @CsvSource({
    "!, NOT, ''",
    "\tLINK_STATE_SLEEPING\t\t\tdb; $05, LABEL, LABEL",
    "<=, LT, EQUAL",
    ">=, GT, EQUAL",
    "==, EQUAL, EQUAL",
    "\\2,LABEL, END_OF_INPUT",
    "\\!, ESCAPE, NOT",
    "\\@, ESCAPE, AT",
  })
  public void scanIfAndMacroOperators(String sourceLine, String operator1, String operator2) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeSpc700.opcodes());

    var token = scanner.getNextToken();

    assertEquals(TokenTypes.valueOf(operator1), token.getType());
    if (!(operator2 == null || operator2.isEmpty())) {
      token = scanner.getNextToken();
      assertEquals(TokenTypes.valueOf(operator2), token.getType());
    }
    assertFalse(sourceLine.endsWith(";"));
  }

  @Test
  public void complexScanTest() {
    var sourceLine = "NUM_SEED_TREES*8";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(TestUtils.toStream(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeSpc700.opcodes());
    assertEquals(TokenTypes.LABEL, scanner.getNextToken().getType());
    assertEquals(TokenTypes.MULTIPLY, scanner.getNextToken().getType());
    assertEquals(TokenTypes.NUMBER, scanner.getNextToken().getType());
  }

  public static String generateDirectiveLine(String pattern, boolean skipFirst) {

    StringBuilder builder = new StringBuilder();
    int patternIndex = 0;

    char patternCharacter = pattern.charAt(patternIndex);

    while (patternCharacter != ' ' && skipFirst) {

      builder.append(patternCharacter);
      patternIndex = patternIndex + 1;
      if (patternIndex < pattern.length()) {
        patternCharacter = pattern.charAt(patternIndex);
      } else {
        break;
      }
    }

    for (; patternIndex <= pattern.length(); patternIndex++) {
      if (patternIndex < pattern.length()) {
        patternCharacter = pattern.charAt(patternIndex);
      } else {
        break;
      }
      switch (patternCharacter) {
        case ')':
          break;
        case 'x':
          builder.append(random.nextInt(256));
          break;
        case 'f':
          builder.append(random.nextInt(256)).append(".").append(random.nextInt(10));
          break;
        case 'c':
          builder.append((char) (random.nextInt(26) + 'A'));
          break;
        case 's':
        case 'l':
          builder.append('"').append(randomString(10)).append('"');
          break;
        case 't':
          builder.append("x != y");
          break;
        case 'e':
          builder.append("(5 + 6)");
          break;
        case '{':
          {
            patternIndex++;
            var newPatternBuilder = new StringBuilder();
            var test = pattern.charAt(patternIndex);
            while (test != '}') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }

            var newPattern = newPatternBuilder.toString();
            builder.append(
                generateDirectiveLine(
                    "" + newPattern.charAt(random.nextInt(newPattern.length())), false));
          }
          break;
        case '[':
          {
            patternIndex++; // ]
            if (pattern.charAt(++patternIndex) == '(') {

              var newPatternBuilder = new StringBuilder();
              newPatternBuilder.append('(');
              var test = pattern.charAt(++patternIndex);
              while (test != ')') {
                newPatternBuilder.append(test);
                test = pattern.charAt(++patternIndex);
              }
              newPatternBuilder.append(')');

              builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
              builder.append(',');
              builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
              builder.append(',');
              builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
            } else {

              var newPatternBuilder = new StringBuilder();
              newPatternBuilder.append('{');
              var test = pattern.charAt(++patternIndex);
              while (test != '}') {
                newPatternBuilder.append(test);
                test = pattern.charAt(++patternIndex);
              }
              newPatternBuilder.append('}');
              var newPattern = newPatternBuilder.toString();

              builder.append(
                  generateDirectiveLine(
                      "" + newPattern.charAt(random.nextInt(newPattern.length() - 2) + 1), false));
              builder.append(',');
              builder.append(
                  generateDirectiveLine(
                      "" + newPattern.charAt(random.nextInt(newPattern.length() - 2) + 1), false));
              builder.append(',');
              builder.append(
                  generateDirectiveLine(
                      "" + newPattern.charAt(random.nextInt(newPattern.length() - 2) + 1), false));
            }
          }
          break;
        case '?':
          {
            //                    if (!random.nextBoolean()) {
            //                        break;
            //                    }
            patternIndex++; // ?

            if (pattern.charAt(patternIndex++) == '(') { // (
              var newPatternBuilder = new StringBuilder();
              newPatternBuilder.append('(');
              var test = pattern.charAt(patternIndex);
              while (test != ')') {
                newPatternBuilder.append(test);
                test = pattern.charAt(++patternIndex);
              }
              newPatternBuilder.append(')');
              builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
            } else {
              // assume }
              var newPatternBuilder = new StringBuilder();

              var test = pattern.charAt(patternIndex);
              while (test != '}') {
                newPatternBuilder.append(test);
                test = pattern.charAt(++patternIndex);
              }

              String[] choices = newPatternBuilder.toString().split("\\|");
              builder.append(choices[random.nextInt(choices.length)]);
            }
          }
          break;
        case '(':
          {
            patternIndex++; // (

            var newPatternBuilder = new StringBuilder();
            var test = pattern.charAt(patternIndex);
            while (test != ')') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }
            builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
          }
          break;

        default:
          builder.append(patternCharacter);
      }
    }
    return builder.toString();
  }

  private static String randomString(int i) {
    var b = new StringBuilder(i);
    for (int x = 0; x < i; x++) {
      b.append((char) (random.nextInt(26) + 'A'));
    }
    return b.toString();
  }
}
