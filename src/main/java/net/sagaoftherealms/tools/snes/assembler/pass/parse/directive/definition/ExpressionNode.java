package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

/**
 * This class represents an expression that defines the size for a definition in a struct, enum, etc
 */
public class ExpressionNode extends Node {

  private TokenTypes operation;

  public ExpressionNode() {
    super(NodeTypes.SIZE_EXPRESION);
  }

  public TokenTypes getOperationType() {
    return operation;
  }

  public void setOperationType(TokenTypes type) {
    this.operation = type;
  }
  
  public int evaluateInt() {
    if (operation == null) {
      return Integer.parseInt(((ConstantNode)getChildren().get(0)).getValue());
    } else if (operation == TokenTypes.MULTIPLY) {
      var leftNode = getChildren().get(0);
      var rightNode = getChildren().get(1);
      int leftValue = 0;
      int rightValue = 0;
      if (leftNode.getType().equals(NodeTypes.SIZE_EXPRESION)) {
        leftValue = ((ExpressionNode)leftNode).evaluateInt();
      } else {
        leftValue = ((ConstantNode)leftNode).getValueAsInt();
      }

      if (rightNode.getType().equals(NodeTypes.SIZE_EXPRESION)) {
        rightValue = ((ExpressionNode)rightNode).evaluateInt();
      } else {
        rightValue = ((ConstantNode)rightNode).getValueAsInt();
      }
      return leftValue * rightValue;
    }
    throw new IllegalStateException("Not implemented");
  }
  
}
