package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum, etc
 */
public class NumericExpressionNode extends ExpressionNode<Integer> {

  public enum OperationType {MULTIPLY, ADD, DIVIDE, SUBTRACT, LEFT_SHIFT, RIGHT_SHIFT, GREATER_THAN, LESS_THAN, LESS_THAN_OR_EQUAL, AND, OR, EQUALS, GREATER_THAN_OR_EQUAL};

  private OperationType operation;

  public NumericExpressionNode() {
    super(NodeTypes.NUMERIC_EXPRESION);
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
      return Integer.parseInt(((ConstantNode) getChildren().get(0)).getValue());
    }

    var leftNode = getChildren().get(0);
    var rightNode = getChildren().get(1);
    int leftValue = 0;
    int rightValue = 0;

    if (leftNode.getType().equals(NodeTypes.NUMERIC_EXPRESION)) {
      leftValue = ((NumericExpressionNode) leftNode).evaluate();
    } else {
      leftValue = ((ConstantNode) leftNode).getValueAsInt();
    }

    if (rightNode.getType().equals(NodeTypes.NUMERIC_EXPRESION)) {
      rightValue = ((NumericExpressionNode) rightNode).evaluate();
    } else {
      rightValue = ((ConstantNode) rightNode).getValueAsInt();
    }

    switch (operation) {

      case MULTIPLY:
        return leftValue * rightValue;
      case ADD:
        return leftValue + rightValue;

      case DIVIDE:
        return leftValue / rightValue;

      case SUBTRACT:
        return leftValue - rightValue;
      case LEFT_SHIFT:
        return leftValue << rightValue;
      case RIGHT_SHIFT:
        return leftValue >> rightValue;

      case GREATER_THAN:
        return leftValue > rightValue?0:1;

      case LESS_THAN:
        return leftValue >= rightValue?0:1;

      case LESS_THAN_OR_EQUAL:
        return leftValue <= rightValue?0:1;

      case AND:
        return leftValue & rightValue;

      case OR:
        return leftValue | rightValue;
      case EQUALS:
        return leftValue == rightValue?0:1;

      case GREATER_THAN_OR_EQUAL:
        return leftValue >= rightValue?0:1;
    }

    throw new IllegalStateException("Not implemented");
  }
}
