package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class NegateIdentifierNode extends IdentifierNode {

  public NegateIdentifierNode(Token token) {
    super(token);
  }

  @Override
  public Integer evaluate() {
    return -1 * super.evaluate();
  }
}
