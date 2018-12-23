package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.NumericExpressionNode;

public class RamsectionArgumentsNode extends DirectiveArgumentsNode {
  public enum RamsectionArguments {
    BANK,
    NAME,
    SLOT,
    APPEND_TO,
    ALIGN
  }

  public RamsectionArgumentsNode() {
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
  }

  public void put(RamsectionArguments key, String value) {
    switch (key) {
      case NAME:
        arguments.set(0, new StringExpressionNode(value));
        break;
      case BANK:
        arguments.set(1, new StringExpressionNode(value));
        break;
      case SLOT:
        arguments.set(2, new StringExpressionNode(value));
        break;

      case ALIGN:
        arguments.set(3, new StringExpressionNode(value));
        break;

      case APPEND_TO:
        arguments.set(4, new StringExpressionNode(value));
        break;
    }
  }

  public String get(RamsectionArguments key) {
    switch (key) {
      case NAME:
        return (String) arguments.get(0).evaluate();
      case BANK:
        return (String) arguments.get(1).evaluate();
      case SLOT:
        return (String) arguments.get(2).evaluate();

      case ALIGN:
        return (String) arguments.get(3).evaluate();

      case APPEND_TO:
        return (String) arguments.get(4).evaluate();
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
