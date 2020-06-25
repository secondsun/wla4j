package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class ConstantNode extends NumericExpressionNode {
  private String value;

  public ConstantNode(int value, Token token) {
    super(NodeTypes.NUMERIC_CONSTANT, token);
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
