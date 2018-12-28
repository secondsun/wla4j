package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

/**
 * This class represents a label definition.
 *
 * <p>Labels have a label (the name) and a size in bytes
 */
public class DefinitionNode extends Node {

  private final String label;
  private String structName;
  private NumericExpressionNode size;

  public DefinitionNode(String label, Token token) {
    super(NodeTypes.DIRECTIVE_BODY, token);
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public NumericExpressionNode getSize() {
    return size;
  }

  /**
   * Sets the size in bytes
   *
   * @param size number of bytes
   */
  public void setSize(int size, Token token) {
    NumericExpressionNode node = new NumericExpressionNode(token);
    node.addChild(new ConstantNode(size, token));
    this.size = node;
  }

  public void setSize(NumericExpressionNode size) {
    this.size = size;
  }

  public Optional<String> getStructName() {
    return Optional.ofNullable(structName);
  }

  public void setStructName(String structName) {
    this.structName = structName;
  }
}
