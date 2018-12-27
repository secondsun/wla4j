package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

public class LowByteNode extends NumericExpressionNode {

  @Override
  public Integer evaluate() {
    return super.evaluate() & 0x0FF;
  }
}
