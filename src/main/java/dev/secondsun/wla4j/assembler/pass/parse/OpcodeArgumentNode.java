package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class OpcodeArgumentNode extends Node {

  private final Token token;

  public OpcodeArgumentNode(Token token) {
    super(NodeTypes.OPCODE_ARGUMENT, token);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

  @Override
  public String toString() {
    return token.getString();
  }
}
