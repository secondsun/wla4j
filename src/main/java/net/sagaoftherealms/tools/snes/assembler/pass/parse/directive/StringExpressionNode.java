package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionNode;

public class StringExpressionNode extends ExpressionNode<String> {

  private final String value;

  public StringExpressionNode(String value) {
    super(NodeTypes.STRING_EXPRESSION);
    this.value = value;
  }

  @Override
  public String evaluate() {
    return value;
  }
}
