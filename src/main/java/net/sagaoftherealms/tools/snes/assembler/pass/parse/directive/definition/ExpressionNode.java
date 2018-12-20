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
    }
    throw new IllegalStateException("Not implemented");
  }
  
}
