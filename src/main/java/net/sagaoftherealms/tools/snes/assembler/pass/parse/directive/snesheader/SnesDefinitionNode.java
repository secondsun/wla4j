package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader;

import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

/**
 * This class represents a label definition.
 *
 * <p>Labels have a label (the name) and a size in bytes
 */
public class SnesDefinitionNode extends Node {

  private final String label;
  private StringExpressionNode name;
  private NumericExpressionNode size;

  public SnesDefinitionNode(String label, Token token) {
    super(NodeTypes.SNES_HEADER_DEFINITION, token);
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
    this.addChild(node);
    this.size = node;
  }

  public void setSize(NumericExpressionNode size) {
    this.size = size;
  }

  public Optional<String> getName() {
    if (name == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(name.evaluate());
    }
  }

  public void setName(String name, Token token) {
    StringExpressionNode node = new StringExpressionNode(name, token);
    this.addChild(node);
    this.name = node;
  }
}
