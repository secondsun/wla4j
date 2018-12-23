package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;

public class EnumArgumentsNode extends DirectiveArgumentsNode {

  public EnumArgumentsNode() {
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    // Add three blank arguments
  }

  public void put(EnumParser.KEYS key, String value) {
    switch (key) {
      case ORDINAL:
        arguments.set(1, new StringExpressionNode(value));
        break;
      case EXPORT:
        arguments.set(2, new StringExpressionNode(value));
        break;
      case ADDRESS:
        arguments.set(0, new StringExpressionNode(value));
        break;
    }
  }

  public String get(EnumParser.KEYS key) {
    switch (key) {
      case ORDINAL:
        return (String) arguments.get(1).evaluate();
      case EXPORT:
        return (String) arguments.get(2).evaluate();
      case ADDRESS:
        return (String) arguments.get(0).evaluate();
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
