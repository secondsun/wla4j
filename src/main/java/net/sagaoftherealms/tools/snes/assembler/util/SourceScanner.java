package net.sagaoftherealms.tools.snes.assembler.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/** A stateful object that is used to read data from a {@link SourceFileDataMap} */
public class SourceScanner {

  private final SourceFileDataMap source;
  private final List<String> opCodes;
  private int lineNumber = 0;
  private int linePosition = 0;
  private boolean newLineBeginning = true;

  public SourceScanner(SourceFileDataMap source, OpCode[] opcodes) {
    this.source = source;
    this.opCodes =
        Arrays.asList(opcodes)
            .stream()
            .map(OpCode::getOp)
            .map(code -> code.split(" ")[0])
            .collect(Collectors.toList());
  }

  public SourceDataLine getNextLine() {
    linePosition = 0;
    newLineBeginning = true;
    return source.getLine(++lineNumber);
  }

  public SourceDataLine getCurrentLine() {
    return source.getLine(lineNumber);
  }

  public Token getNextToken() {
    return getNextToken(true);
  }

  private Token getNextToken(boolean advance) {
    if (endOfInput()) {
      return new Token(getCurrentLine(), "", TokenTypes.END_OF_INPUT);
    }

    String tokenString = getNextTokenString(advance);
    TokenTypes type;
    final List<Character> operators =
        Arrays.asList(
            ',', '|', '&', '^', '+', '-', '#', '~', '*', '/', '<', '>', '[', ']', '(', ')', '!',
            '=', '\\', '@');
    final List<String> sizeTokens = Arrays.asList(".b", ".w", ".l", ".B", ".W", ".L");

    if (tokenString.equals("\n")) {
      type = TokenTypes.EOL;
    } else if (tokenString.startsWith("\"")) {
      type = TokenTypes.STRING;
      // trim quotes;
      tokenString = tokenString.substring(1, tokenString.length() - 1);
    } else if (sizeTokens.contains(tokenString)) {
      type = TokenTypes.SIZE;
    } else if (tokenString.startsWith(".")) {
      type = TokenTypes.DIRECTIVE;
    } else if (tokenString.matches(TokenUtil.DECIMAL_NUMBER_REGEX)
        || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_0)
        || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_$)
        || tokenString.matches(TokenUtil.CHARACTER_NUMBER_REGEX)
        || tokenString.matches(TokenUtil.BINARY_NUMBER_REGEX)) {
      type = TokenTypes.NUMBER;
    } else if ((!tokenString.equals("@"))
        && (Character.isAlphabetic(tokenString.charAt(0))
            || tokenString.charAt(0) == '_'
            || tokenString.charAt(0) == ':'
            || tokenString.charAt(0) == '@')) {
      if (opCodes.contains(tokenString.toUpperCase())) {
        type = TokenTypes.OPCODE;
      } else {
        type = TokenTypes.LABEL;
      }
    } else if (tokenString.length() == 1 && operators.contains(tokenString.charAt(0))) {
      type = operatorType(tokenString.charAt(0));
    } else if (tokenString.matches("\\-+:?") || tokenString.matches("\\++:?")) {
      type = TokenTypes.LABEL;
    } else if (tokenString.matches("\\\\\\d+?")) {
      type = TokenTypes.LABEL;
    } else {
      throw new IllegalArgumentException("Could not getString TokenType for " + tokenString + "@" + getCurrentLine().toString());
    }

    return new Token(getCurrentLine(), tokenString, type);
  }

  private String getNextTokenString(boolean advance) {

    final List<Character> operators =
        Arrays.asList(
            ',', '|', '&', '^', '+', '-', '#', '~', '*', '/', '<', '>', '[', ']', '(', ')', '!',
            '=', '\\', '@');

    if (lineNumber == 0) {
      getNextLine();
    }

    // getString line where we left off reading
    var line = getCurrentLine();
    var sourceString = line.getDataLine();
    var initialLinePosition = linePosition;
    var initialLineNumber = lineNumber;
    var initialNewline = newLineBeginning;
    try {
      while (linePosition >= sourceString.length()) {
        getNextLine();
        line = getCurrentLine();
        sourceString = line.getDataLine();
        if (linePosition < sourceString.length()) {
          return "\n"; // collapse multiple newlines
        }
      }

      char character = sourceString.charAt(linePosition);
      linePosition++;

      // Consume leading whitespace
      while (Character.isWhitespace(character)) {
        character = sourceString.charAt(linePosition);
        linePosition++;
      }

      if (character == '"') {
        return stringToken(sourceString);
      } else if (character == '.') {
        return directiveToken(sourceString);
      } else if (Character.isDigit(character) || character == '$' || character == '%') {
        return numberToken(sourceString, character);
      } else if (character
          == '\'') { // Escape character unless it is \1 \2 etc then it is a macro label.
        return characterToken(sourceString);
      } else if (Character.isAlphabetic(character)
          || character == '_'
          || character == '@'
          || (character == '\\' && Character.isDigit(sourceString.charAt(linePosition)))
          || character == ':') {
        return labelToken(sourceString, character);
      } else if (operators.contains(character)) {
        if (character == '-' || character == '+') { // This is a label of the --- or +++ variety
          if ((linePosition) < sourceString.length()) {
            var nextCharacter = sourceString.charAt(linePosition);
            String toReturn = character + "";
            while (((linePosition) < sourceString.length())
                && nextCharacter == character) { // This is a label of the --- or +++ variety
              toReturn += nextCharacter;
              linePosition++;
              if ((linePosition) < sourceString.length()) {
                nextCharacter = sourceString.charAt(linePosition);
              }
            }

            if ((linePosition) < sourceString.length()
                && sourceString.charAt(linePosition) == ':') {
              toReturn += nextCharacter;
              linePosition++;
            }

            return toReturn;
          }
        }
        return "" + character;
      }

      return null;
    } finally {
      if (!advance) {
        linePosition = initialLinePosition;
        lineNumber = initialLineNumber;
      }
    }
  }

  private String labelToken(String sourceString, char character) {
    final List<Character> operators =
        Arrays.asList(
            ',', '|', '&', '^', '+', '-', '#', '~', '*', '/', '<', '>', '[', ']', '(', ')', '!',
            '=');

    StringBuilder builder = new StringBuilder();

    do {
      builder.append(character);

      if (linePosition >= sourceString.length()) {
        break;
      }

      character = sourceString.charAt(linePosition);
      linePosition++;

      if (character == '.') {
        if (linePosition >= sourceString.length()) {
          break;
        }
        var peekCharacter = sourceString.charAt(linePosition);
        if ((peekCharacter == 'b'
            || peekCharacter == 'w'
            || peekCharacter == 'l'
            || peekCharacter == 'B'
            || peekCharacter == 'W'
            || peekCharacter == 'L')) { // labels can have a size which is .b .l  or .w
          if ((linePosition + 1) >= sourceString.length()) {
            if (opCodes.contains(builder.toString() + "." + peekCharacter)) {
              builder.append(builder.toString() + "." + peekCharacter);
              linePosition += 2;
            }
            break;
          }
          var peekCharacter2 = sourceString.charAt(linePosition + 1);
          if (Character.isWhitespace(peekCharacter2) || operators.contains(peekCharacter2)) {
            if (opCodes.contains(builder.toString() + "." + peekCharacter)) {
              builder.append("." + peekCharacter);
              linePosition += 2;
            }
            break;
          }
        }
        builder.append(character);
        character = sourceString.charAt(linePosition);
        linePosition++;
      }

    } while (!Character.isWhitespace(character)
        && character != '.'
        && !operators.contains(character));

    if (character == '.' || operators.contains(character)) {
      linePosition--;
    }

    return builder.toString().trim();
  }

  private String characterToken(String sourceString) {
    char character = '\'';

    StringBuilder builder = new StringBuilder().append(character);

    if (linePosition >= sourceString.length()) {
      throw new IllegalStateException(
          "Unterminated character at " + sourceString + ":" + getCurrentLine());
    }
    character = sourceString.charAt(linePosition);
    linePosition++;
    builder.append(character);

    character = sourceString.charAt(linePosition);
    if (character != '\'') {
      throw new IllegalStateException(
          "Unterminated character at " + sourceString + ":" + getCurrentLine());
    }
    linePosition++;
    builder.append(character);

    return builder.toString().trim();
  }

  private String numberToken(String sourceString, char character) {
    var chars =
        new Character[] {
          'A', 'B', 'C', 'D', 'E', 'F', 'H', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
          'b', 'c', 'd', 'e', 'f', 'h', '.', '$', '%'
        };
    final List<Character> allowedCharacters = Arrays.asList(chars);
    StringBuilder builder = new StringBuilder().append(character);

    do {

      if (!allowedCharacters.contains(character)) {
        linePosition--;
        break;
      }

      if (linePosition >= sourceString.length()) {
        break;
      }
      character = sourceString.charAt(linePosition);
      linePosition++;
      if (character == '.') {
        // Handle optional size
        if (!Character.isDigit(
            sourceString.charAt(linePosition))) { // character is not a digit, may be size.
          linePosition--;
          return builder.toString().trim();
        }
      }
      if (!allowedCharacters.contains(character)) {
        linePosition--;
        break;
      }
      builder.append(character);
    } while (allowedCharacters.contains(character));

    return builder.toString().trim();
  }

  private String directiveToken(String sourceString) {
    StringBuilder builder = new StringBuilder().append(".");
    if (!(Character.isAlphabetic(sourceString.charAt(linePosition))
        || Character.isDigit(sourceString.charAt(linePosition)))) {
      // TODO: Real Error Handling
      throw new IllegalStateException("Empty directive at " + sourceString);
    }
    char character;
    do {
      if (linePosition >= sourceString.length()) {
        break;
      }
      character = sourceString.charAt(linePosition);
      linePosition++;
      builder.append(character);
    } while (Character.isAlphabetic(character) || Character.isDigit(character));
    return builder.toString().trim();
  }

  private String stringToken(String sourceString) {
    StringBuilder builder = new StringBuilder().append("\"");
    char character;
    do {
      if (linePosition >= sourceString.length()) {
        // TODO: Real error handling.
        throw new IllegalStateException("Unterminated String " + sourceString);
      }
      character = sourceString.charAt(linePosition);
      linePosition++;
      builder.append(character);
    } while (character != '"');
    return builder.toString();
  }

  private TokenTypes operatorType(char operatorCharacter) {

    switch (operatorCharacter) {
      case ',':
        return TokenTypes.COMMA;
      case '|':
        return TokenTypes.OR;
      case '&':
        return TokenTypes.AND;
      case '^':
        return TokenTypes.POWER;
      case '+':
        return TokenTypes.PLUS;
      case '-':
        return TokenTypes.MINUS;
      case '#':
        return TokenTypes.MODULO;
      case '~':
        return TokenTypes.XOR;
      case '*':
        return TokenTypes.MULTIPLY;
      case '/':
        return TokenTypes.DIVIDE;
      case '<':
        return TokenTypes.LT;
      case '>':
        return TokenTypes.GT;
      case ']':
        return TokenTypes.RIGHT_BRACKET;
      case '[':
        return TokenTypes.LEFT_BRACKET;
      case ')':
        return TokenTypes.RIGHT_PAREN;
      case '(':
        return TokenTypes.LEFT_PAREN;
      case '!':
        return TokenTypes.NOT;
      case '=':
        return TokenTypes.EQUAL;
      case '\\':
        return TokenTypes.ESCAPE;
      case '@':
        return TokenTypes.AT;
      default:
        throw new IllegalArgumentException("Unknown Operator Type");
    }
  }

  public boolean endOfInput() {
    if (lineNumber > source.lineCount()) {
      return true;
    }
    SourceDataLine currentLine;
    if (lineNumber == 0) {
      currentLine = source.getLine(1);
    } else {
      currentLine = getCurrentLine();
    }

    if (linePosition >= currentLine.getDataLine().length()) {
      return lineNumber >= source.lineCount();
    }
    return lineNumber > source.lineCount();
  }

  public Token peekNextToken() {
    return getNextToken(false);
  }

  public void reset() {
    lineNumber = 0;
    linePosition = 0;
    newLineBeginning = true;
  }

}
