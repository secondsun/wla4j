package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

public class LowByteNode extends NumericExpressionNode {

  private final ExpressionNode value;

  public LowByteNode(ExpressionNode value) {
    this.value = value;
  }

  @Override
  public Integer evaluate() {
    return ((int) value.evaluate()) & 0x0FF;
  }
}
