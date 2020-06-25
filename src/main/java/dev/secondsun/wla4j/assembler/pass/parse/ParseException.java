package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class ParseException extends RuntimeException {

  private final Token problemToken;

  public ParseException(String message, Token token) {
    super(message + "\n\t" + token);
    this.problemToken = token;
  }

  public ParseException(String message, Exception e, Token token) {
    super(message + "\n\t" + token, e);
    this.problemToken = token;
  }

  public Token getProblemToken() {
    return problemToken;
  }
}
