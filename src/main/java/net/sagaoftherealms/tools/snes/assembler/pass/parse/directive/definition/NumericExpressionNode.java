package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum,
 * etc
 */
public class NumericExpressionNode extends ExpressionNode<Integer> {

  public enum OperationType {MULTIPLY, ADD, DIVIDE, SUBTRACT, LEFT_SHIFT, RIGHT_SHIFT, GREATER_THAN, LESS_THAN, LESS_THAN_OR_EQUAL, AND, OR, EQUALS, NOT_EQUAL, GREATER_THAN_OR_EQUAL}

  ;

  private OperationType operation;
  private static final List<NodeTypes> ALLOWED_TYPES = Arrays.asList(NodeTypes.NUMERIC_EXPRESION,NodeTypes.NUMERIC_CONSTANT, NodeTypes.IDENTIFIER_EXPRESSION);
  public NumericExpressionNode() {
    super(NodeTypes.NUMERIC_EXPRESION);
  }

  public NumericExpressionNode(NodeTypes type) {
    super(type);

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
        return leftValue > rightValue ? 1 : 0;

      case LESS_THAN:
        return leftValue >= rightValue ? 1 : 0;

      case LESS_THAN_OR_EQUAL:
        return leftValue <= rightValue ? 1 : 0;

      case AND:
        return leftValue & rightValue;

      case OR:
        return leftValue | rightValue;
      case EQUALS:
        return leftValue == rightValue ? 1 : 0;
      case NOT_EQUAL:
        return leftValue != rightValue ? 1 : 0;

      case GREATER_THAN_OR_EQUAL:
        return leftValue >= rightValue ? 1 : 0;

    }

    throw new IllegalStateException("Not implemented" + operation);
  }
}
