package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

/**
 * This class represents a label definition.
 *
 * <p>Labels have a label (the name) and a size in bytes
 */
public class DefinitionNode extends Node {

  private final String label;
  private String structName;
  private ExpressionNode size;

  public DefinitionNode(String label) {
    super(NodeTypes.DIRECTIVE_BODY);
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public ExpressionNode getSize() {
    return size;
  }

  /**
   * Sets the size in bytes
   *
   * @param size number of bytes
   */
  public void setSize(int size) {
    ExpressionNode node = new ExpressionNode();
    node.addChild(new ConstantNode(NodeTypes.NUMERIC_CONSTANT));
    ((ConstantNode)node.getChildren().get(0)).setValue(size + "");
    this.size = node;
  }

  public void setSize(ExpressionNode size) {
    this.size = size;
  }
  
  public Optional<String> getStructName() {
    return Optional.ofNullable(structName);
  }

  public void setStructName(String structName) {
    this.structName = structName;
  }
}
