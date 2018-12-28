package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.OperationType;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum, etc
 */
public class NumericExpressionNode extends ExpressionNode<Integer> {

  private OperationType operation;

  public NumericExpressionNode(Token token) {
    super(NodeTypes.NUMERIC_EXPRESION, token);
  }

  public NumericExpressionNode(NodeTypes type, Token token) {
    super(type, token);
  }

  public OperationType getOperationType() {
    return operation;
  }

  public void setOperationType(OperationType type) {
    this.operation = type;
  }

  @Override
  public Integer evaluate() {
    if (operation == null) {
      if (getType().equals(NodeTypes.IDENTIFIER_EXPRESSION)) {
        return 0; // todo lookup label value
      }
      return Integer.parseInt(((ConstantNode) getChildren().get(0)).getValue());
    }

    var leftNode = getChildren().get(0);
    var rightNode = getChildren().get(1);
    int leftValue = 0;
    int rightValue = 0;

    if (!(leftNode instanceof ConstantNode)) {
      leftValue = ((NumericExpressionNode) leftNode).evaluate();
    } else {
      leftValue = ((ConstantNode) leftNode).getValueAsInt();
    }

    if (!(rightNode instanceof ConstantNode)) {
      rightValue = ((NumericExpressionNode) rightNode).evaluate();
    } else {
      rightValue = ((ConstantNode) rightNode).getValueAsInt();
    }

    return operation.evaluate(leftValue, rightValue);
  }
}
