package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class NegateIdentifierNode extends IdentifierNode {

  public NegateIdentifierNode(Token token) {
    super(token);
  }

  @Override
  public Integer evaluate() {
    return -1 * super.evaluate();
  }
}
