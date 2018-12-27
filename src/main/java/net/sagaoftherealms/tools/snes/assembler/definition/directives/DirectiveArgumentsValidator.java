package net.sagaoftherealms.tools.snes.assembler.definition.directives;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MINUS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;

import java.util.Arrays;
import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public final class DirectiveArgumentsValidator {

  private final String pattern;
  private int patternIndex = 0;
  private Matcher specialMatcher;

  public DirectiveArgumentsValidator(String pattern) {
    this.pattern = pattern;
    if (pattern.contains("}") && !pattern.endsWith("}")) {
      if (pattern.split("}")[1].contains(",")) {
        throw new IllegalArgumentException("Directives may not have commas after arrays");
      }
    }
  }

  public Optional<Node> accept(Token token, SourceParser parser) {
    if (matches(token)) {
      if (specialMatcher == null) {
        advancePattern();
      }
      return Optional.ofNullable(parser.nextNode());
    } else {
      if (specialMatcher != null && specialMatcher.isSatisfied()) {
        advancePattern();
        if (matches(token)) {
          return Optional.ofNullable(parser.nextNode());
        } else {
          return Optional.empty();
        }
      }
      return Optional.empty();
    }
  }

  private void advancePattern() {
    patternIndex++;
    while (patternIndex < pattern.length() && pattern.charAt(patternIndex) == ' ') {
      patternIndex++;
    }
  }

  // x = a whole number
  // f = a number with a decimal part
  // c = a character
  // s = a String value (expands to "some text"
  // l = a label (which will be a string)

  // t = a boolean expression
  // e = a integer expression

  // {x|y} One of x, y...
  // []{} = A comma separated List of types in the braces (see .DB in
  // https://wla-dx.readthedocs.io/en/latest/asmdiv.html)

  // ? = Optional

  // (pattern) a pattern

  /**
   * Look at the current element in the directive pattern and return true if the token matches that
   * pattern.
   */
  private boolean matches(Token token) {
    if (patternIndex >= pattern.length()) {
      return false;
    }

    if (specialMatcher != null) {
      boolean matches = specialMatcher.match(token);
      if (matches) {
        return true;
      } else {
        if (specialMatcher.isSatisfied()) {
          specialMatcher = null;
          advancePattern();
          return checkHasMore() && matches(token);
        } else {
          return false;
        }
      }
    }

    var chara = pattern.charAt(patternIndex);
    switch (chara) {
      case 'x': // x = a whole number
        return matchInt(token);
      case 'f': // f = a number with a decimal part
        return matchFloat(token);
      case 'c': // c = a character
        return matchChar(token);
      case 'l': // l = a label (which will be a string)
        return matchLabel(token);
      case 's': // s = a String value (expands to "some text"
        return matchString(token);
      case '{':
        beginOneOf();
        return matches(token);
      case '[':
        begingArray();
        return matches(token);
      case '?':

      case 'e': // e = a integer expression
        System.out.println(token);
        throw new IllegalStateException("Expression No Longer used here.");

      case ',':
        if (token.getString().equals(",")) {
          return true;
        } else {
          advancePattern(); // pattern is on comma, commas are optional, scoot on along.
          return matches(token);
        }
      default:
        throw new IllegalStateException(
            "This should no longer get called.  Write a parser. From " + token);
    }
  }

  private void beginOneOf() {
    this.specialMatcher = new OneOfMatcher(oneOfPattern());
  }

  private String oneOfPattern() {
    StringBuilder oneOfPatternBuilder = new StringBuilder();
    patternIndex++;
    while (pattern.charAt(patternIndex) != '}') {
      oneOfPatternBuilder.append(pattern.charAt(patternIndex));
      patternIndex++;
    }
    return oneOfPatternBuilder.toString();
  }

  private boolean matchString(Token token) {
    return token.getType().equals(TokenTypes.STRING);
  }

  private boolean matchLabel(Token token) {
    return token.getType().equals(LABEL);
  }

  private boolean matchChar(Token token) {
    return token.getType().equals(NUMBER) && token.getString().matches("'[\\w\\d]'");
  }

  private boolean matchFloat(Token token) {
    return token.getType().equals(NUMBER)
        && token
            .getString()
            .matches(
                "^\\d*\\.\\d+$"); // matches an optional number, a period, then any number of digits
  }

  private void begingArray() {
    this.specialMatcher = new ArrayMatcher(arrayPattern());
  }

  private String arrayPattern() {
    patternIndex++;
    StringBuilder arrayPatternBuilder = new StringBuilder();
    if (pattern.charAt(patternIndex) != ']') {
      throw new IllegalStateException("Invalid array argument");
    }
    patternIndex++;
    if (pattern.charAt(patternIndex) != '{') {
      throw new IllegalStateException("Invalid array argument");
    }
    patternIndex++;
    while (pattern.charAt(patternIndex) != '}') {
      arrayPatternBuilder.append(pattern.charAt(patternIndex));
      patternIndex++;
    }
    return arrayPatternBuilder.toString();
  }

  public boolean checkHasMore() {
    return patternIndex < pattern.length();
  }

  private boolean matchInt(Token token) {
    return token.getType().equals(NUMBER)
        && (token.getString().startsWith("$")
            || token.getString().matches("^\\d+$")); // any number of digits
  }

  interface Matcher {

    boolean match(Token token);

    /**
     * Is the matcher satisfied that the pattern has been matched and the validator can advance the
     * master pattern
     *
     * @return if the master validator should clear the special matcher and advance the pattern to
     *     the next argument
     */
    boolean isSatisfied();
  }

  private class ArrayMatcher implements Matcher {

    private final String arrayPattern;
    private boolean expectComma = false;
    private boolean finished = false;

    public ArrayMatcher(String arrayPattern) {
      this.arrayPattern = arrayPattern;
    }

    @Override
    public boolean match(Token token) {
      if (expectComma) {
        if (TokenTypes.COMMA.equals(token.getType())) {
          expectComma = false;
          return true;
        } else {
          finished = true;
          return false;
        }
      }

      for (int arrayPatternIndex = 0;
          arrayPatternIndex < arrayPattern.length();
          arrayPatternIndex++) {
        switch (arrayPattern.charAt(arrayPatternIndex)) {
          case 'x':
            if (matchInt(token)) {
              expectComma = true;
              return true;
            }
            break;
          case 'f':
            if (matchFloat(token)) {
              expectComma = true;
              return true;
            }
            break;
          case 'c':
            if (matchChar(token)) {
              expectComma = true;
              return true;
            }
            break;
          case 's':
            if (matchString(token)) {
              expectComma = true;
              return true;
            }
            break;
          case 'e':
            if (Arrays.asList(TokenTypes.LEFT_PAREN, LABEL, NUMBER).contains(token.getType())) {
              expectComma = true;
              return true;
            }
            break;
          case 'l':
            if (matchLabel(token)) {
              expectComma = true;
              return true;
            }
            break;
          default:
            throw new IllegalStateException("Unexpected pattern character.");
        }
      }
      finished = true;
      return false;
    }

    @Override
    public boolean isSatisfied() {
      return finished;
    }
  }

  private class OneOfMatcher implements Matcher {

    private final String oneOfPattern;
    private boolean hasMatched = false;
    private boolean expressionComplete = false;

    public OneOfMatcher(String oneOfPattern) {
      // consolidate patterns
      // IE if a Label or a Number or an Expression then you can match all of those as just an
      // expression.
      // Then we give priority to expressions in the matcher.  Should even things out.

      if (oneOfPattern.contains("e")) {
        oneOfPattern =
            oneOfPattern.replace("l", "").replace("c", "").replace("f", "").replace("x", "");
      }

      this.oneOfPattern = oneOfPattern;
    }

    @Override
    public boolean match(Token token) {

      if (hasMatched
          && !oneOfPattern.contains(
              "e")) { // As one of implies, it can only match one.  However expressions are hard to
        // do because they require multiple tokens to be matched possible.
        return false;
      }

      switch (token.getType()) {
        case STRING:
          if (oneOfPattern.contains("s")) {
            hasMatched = true;
            expressionComplete = true;
            return true;
          }
          break;
        case NUMBER:
          if (!token.getString().contains(".")) {
            if (oneOfPattern.contains("x")) {
              hasMatched = true;
              expressionComplete = true;
              return true;
            }
          } else {
            if (oneOfPattern.contains("f")) {
              hasMatched = true;
              expressionComplete = true;
              return true;
            }
          }

          if (oneOfPattern.contains("e") && !expressionComplete) {
            expressionComplete = true;
            hasMatched = true;
            return true;
          }

          break;
        case LABEL:
          if (oneOfPattern.contains("l")) {
            hasMatched = true;
            return true;
          }
          if (oneOfPattern.contains("e") && !expressionComplete) {
            hasMatched = true;
            expressionComplete = true;
            return true;
          }
          break;
        case MULTIPLY:
        case AND:
        case MINUS:
        case PLUS:
          if (expressionComplete) {
            expressionComplete = false;
            return true;
          }
          break;
        default:
          return false;
      }

      return false;
    }

    @Override
    public boolean isSatisfied() {
      return hasMatched;
    }
  }
}
