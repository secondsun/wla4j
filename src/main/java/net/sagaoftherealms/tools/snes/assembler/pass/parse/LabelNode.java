package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class LabelNode extends Node {

  private final Token token;

  public LabelNode(Token token) {
    super(NodeTypes.LABEL);
    this.token = token;
  }
}
