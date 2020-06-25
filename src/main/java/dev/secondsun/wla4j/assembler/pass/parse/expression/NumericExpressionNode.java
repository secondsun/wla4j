package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.parse.directive.definition.OperationType;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum, etc
 */
public class NumericExpressionNode extends ExpressionNode<Integer> {

  private OperationType operation;
  private Sizes size;

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

  public void setSize(Sizes size) {
    this.size = size;
  }

  public Sizes getSize() {
    return size;
  }
}
