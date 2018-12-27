package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

public class ConstantNode extends NumericExpressionNode {
  private String value;

  public ConstantNode(int value) {
    super(NodeTypes.NUMERIC_CONSTANT);
    this.value = value + "";
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public int getValueAsInt() {
    return Integer.parseInt(value);
  }

  @Override
  public Integer evaluate() {
    return getValueAsInt();
  }
}
