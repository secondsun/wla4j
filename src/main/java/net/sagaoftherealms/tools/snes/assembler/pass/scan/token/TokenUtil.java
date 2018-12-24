package net.sagaoftherealms.tools.snes.assembler.pass.scan.token;

/** Utilities for transforming a token into typed values */
public final class TokenUtil {

  public static final String CHARACTER_NUMBER_REGEX = "'.'";
  public static final String DECIMAL_NUMBER_REGEX = "\\d+.{0,1}\\d*";
  public static final String HEX_NUMBER_REGEX_0 = "0[A-Fa-f0-9]+h";
  public static final String HEX_NUMBER_REGEX_$ = "\\$[A-Fa-f0-9]+";
  public static final String BINARY_NUMBER_REGEX = "\\%[01]+";

  private TokenUtil() {}

  public static double getDouble(Token token) {
    if (TokenTypes.NUMBER != token.getType()) {
      throw new IllegalArgumentException(
          "Expected a token type of Number, but was " + token.getType());
    }

    var tokenString = token.getString();

    if (tokenString.matches(DECIMAL_NUMBER_REGEX)) {
      return Double.parseDouble(token.getString());
    }

    if (tokenString.matches(HEX_NUMBER_REGEX_0)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("h", ""), 16);
    }

    if (tokenString.matches(HEX_NUMBER_REGEX_$)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("$", ""), 16);
    }

    if (tokenString.matches(BINARY_NUMBER_REGEX)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("%", ""), 2);
    }

    if (tokenString.matches(CHARACTER_NUMBER_REGEX)) {
      return (double) tokenString.charAt(1);
    }

    throw new IllegalArgumentException("Unrecognized number token format " + token.getString());
  }

  public static int getInt(Token token) {
    if (TokenTypes.NUMBER != token.getType()) {
      throw new IllegalArgumentException(
          "Expected a token type of Number, but was " + token.getType());
    }

    var tokenString = token.getString();

    if (tokenString.matches(HEX_NUMBER_REGEX_0)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("h", ""), 16);
    }

    if (tokenString.matches(HEX_NUMBER_REGEX_$)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("$", ""), 16);
    }

    if (tokenString.matches(CHARACTER_NUMBER_REGEX)) {
      return (int) tokenString.charAt(1);
    }

    if (tokenString.matches(BINARY_NUMBER_REGEX)) {
      // Matches a hex number prefixed with 0 and ending with h
      return Integer.parseInt(token.getString().replace("%", ""), 2);
    }

    return (int)
        Double.parseDouble(
            token.getString()); // This looks wrong, but at the moment it makes a test pass so TDD!
  }

  public static String getLabelName(Token token) {
    if (token.getType() != TokenTypes.LABEL) {
      throw new IllegalArgumentException("Token argument was not a label type");
    }
    String labelString = token.getString();

    if (labelString.matches("__+") || labelString.matches("_+f") || labelString.matches("_+b")) {
      // __+ is an anonymous label and _+f/_+b are special commands.
    } else {

      while (labelString.startsWith("@") || labelString.startsWith("_")) {
        labelString = labelString.substring(1);
      }
    }

    return labelString.replace(":", "");
  }
}
