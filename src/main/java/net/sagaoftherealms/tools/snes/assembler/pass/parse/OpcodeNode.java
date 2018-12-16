package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class OpcodeNode extends Node{

  private final Token token;

  public OpcodeNode(Token token) {
    super(NodeTypes.OPCODE);
    this.token = token;
  }
}
