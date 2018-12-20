package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class OpcodeArgumentNode extends Node {

  private final Token token;

  public OpcodeArgumentNode(Token token) {
    super(NodeTypes.OPCODE_ARGUMENT);
    this.token = token;
  }

  public Token getToken() {
    return token;
  }
}
