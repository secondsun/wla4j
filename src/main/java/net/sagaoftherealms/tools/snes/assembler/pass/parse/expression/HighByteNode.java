package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

public class HighByteNode extends NumericExpressionNode {

  @Override
  public Integer evaluate() {
    return (super.evaluate() & 0x0FF00) >> 8;
  }
}
