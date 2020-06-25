package dev.secondsun.wla4j.assembler.definition.directives;

import java.util.Arrays;
import java.util.Optional;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

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
        throw new IllegalStateException(
            "One of expressions should override DirectiveParser.arguments instead of using the GenericDirectiveValidator.");
      case '[':
        begingArray();
        return matches(token);
      case '?':

      case 'e': // e = a integer expression
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

  private boolean matchString(Token token) {
    return token.getType().equals(TokenTypes.STRING);
  }

  private boolean matchLabel(Token token) {
    return token.getType().equals(TokenTypes.LABEL);
  }

  private boolean matchChar(Token token) {
    return token.getType().equals(TokenTypes.NUMBER) && token.getString().matches("'[\\w\\d]'");
  }

  private boolean matchFloat(Token token) {
    return token.getType().equals(TokenTypes.NUMBER)
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
    return token.getType().equals(TokenTypes.NUMBER)
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
            if (Arrays.asList(TokenTypes.LEFT_PAREN, TokenTypes.LABEL, TokenTypes.NUMBER).contains(token.getType())) {
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
}
