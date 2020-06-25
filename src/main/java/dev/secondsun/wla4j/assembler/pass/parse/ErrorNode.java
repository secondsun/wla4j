package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class ErrorNode extends Node {

  private final Exception exception;

  public ErrorNode(Token token, ParseException exception) {
    super(NodeTypes.ERROR, token);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }
}
