package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class HighByteNode extends NumericExpressionNode {

  private final ExpressionNode value;

  public HighByteNode(ExpressionNode value, Token token) {
    super(token);
    this.value = value;
  }

  @Override
  public Integer evaluate() {
    return ((int) value.evaluate() & 0x0FF00) >> 8;
  }
}
