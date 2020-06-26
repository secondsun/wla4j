package dev.secondsun.wla4j.assembler.pass.parse.directive;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class StringExpressionNode extends ExpressionNode<String> {

  private final String value;

  public StringExpressionNode(String value, Token token) {
    super(NodeTypes.STRING_EXPRESSION, token);
    this.value = value;
  }

  @Override
  public String evaluate() {
    return value;
  }
}
