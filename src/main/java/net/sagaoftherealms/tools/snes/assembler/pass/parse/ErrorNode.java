package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

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
