package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum, etc
 */
public class NumericExpressionNode extends ExpressionNode<Integer> {


  private OperationType operation;
  private static final List<NodeTypes> ALLOWED_TYPES =
      Arrays.asList(
          NodeTypes.NUMERIC_EXPRESION, NodeTypes.NUMERIC_CONSTANT, NodeTypes.IDENTIFIER_EXPRESSION);

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

    return operation.evaluate(leftValue, rightValue);

  }
}
