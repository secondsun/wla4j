package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

public class HighByteNode extends NumericExpressionNode {

  private final ExpressionNode value;

  public HighByteNode(
      ExpressionNode value) {
    this.value = value;
  }

  @Override
  public Integer evaluate() {
    return ((int)value.evaluate() & 0x0FF00) >> 8;
  }
}
