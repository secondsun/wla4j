package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class LowByteNode extends NumericExpressionNode {

  private final ExpressionNode value;

  public LowByteNode(ExpressionNode value, Token token) {
    super(token);
    this.value = value;
  }

  @Override
  public Integer evaluate() {
    return ((int) value.evaluate()) & 0x0FF;
  }
}
