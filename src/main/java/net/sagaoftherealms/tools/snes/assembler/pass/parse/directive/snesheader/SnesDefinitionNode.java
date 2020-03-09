package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader;

import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

/**
 * This class represents a SNES header definition.  SNES header directives are snesheader,
 * snesnativevector, and snesemuvector
 */
public class SnesDefinitionNode extends Node {

  private final String key;
  private StringExpressionNode stringValue;
  private NumericExpressionNode numericValue;

  public SnesDefinitionNode(String key, Token token) {
    super(NodeTypes.SNES_HEADER_DEFINITION, token);
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public NumericExpressionNode getNumericValue() {
    return numericValue;
  }

  public void setNumericValue(NumericExpressionNode numericValue) {
    this.numericValue = numericValue;
  }

  public void setNumericValue(int size, Token token) {
    NumericExpressionNode node = new NumericExpressionNode(token);
    node.addChild(new ConstantNode(size, token));
    this.addChild(node);
    this.numericValue = node;
  }

  public Optional<String> getStringValue() {
    if (stringValue == null) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(stringValue.evaluate());
    }
  }

  public void setStringValue(String name, Token token) {
    StringExpressionNode node = new StringExpressionNode(name, token);
    this.addChild(node);
    this.stringValue = node;
  }
}
