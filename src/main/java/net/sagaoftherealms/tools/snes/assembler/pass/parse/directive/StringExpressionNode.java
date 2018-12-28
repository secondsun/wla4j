package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

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
