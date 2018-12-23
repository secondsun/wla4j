package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;

public class SectionArgumentsNode extends DirectiveArgumentsNode {

  public SectionArgumentsNode() {
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    // Add eight blank arguments
  }

  public void put(SectionParser.KEYS key, String value) {
    switch (key) {
      case NAME:
        arguments.set(0, new StringExpressionNode(value));
        break;
      case BANKHEADER:
        arguments.set(1, new StringExpressionNode(value));
        break;
      case NAMESPACE:
        arguments.set(2, new StringExpressionNode(value));
        break;
      case SIZE:
        arguments.set(3, new StringExpressionNode(value));
        break;
      case ALIGN:
        arguments.set(4, new StringExpressionNode(value));
        break;
      case STATUS:
        arguments.set(5, new StringExpressionNode(value));
        break;
      case APPEND_TO:
        arguments.set(6, new StringExpressionNode(value));
        break;
      case RETURNORG:
        arguments.set(7, new StringExpressionNode(value));
        break;
    }
  }

  public String get(SectionParser.KEYS key) {
    switch (key) {
      case NAME:
        return (String) arguments.get(0).evaluate();
      case BANKHEADER:
        return (String) arguments.get(1).evaluate();
      case NAMESPACE:
        return (String) arguments.get(2).evaluate();
      case SIZE:
        return (String) arguments.get(3).evaluate();
      case ALIGN:
        return (String) arguments.get(4).evaluate();
      case STATUS:
        return (String) arguments.get(5).evaluate();
      case APPEND_TO:
        return (String) arguments.get(6).evaluate();
      case RETURNORG:
        return (String) arguments.get(7).evaluate();
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
