package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.NumericExpressionNode;

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
