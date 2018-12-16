package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;

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
        arguments.set(0, value);
        break;
      case BANKSECTION:
        arguments.set(1, value);
        break;
      case NAMESPACE:
        arguments.set(2, value);
        break;
      case SIZE:
        arguments.set(3, value);
        break;
      case ALIGN:
        arguments.set(4, value);
        break;
      case STATUS:
        arguments.set(5, value);
        break;
      case APPEND_TO:
        arguments.set(6, value);
        break;
      case RETURNORG:
        arguments.set(7, value);
        break;
    }
  }

  public String get(SectionParser.KEYS key) {
    switch (key) {
      case NAME:
        return arguments.get(0);
      case BANKSECTION:
        return arguments.get(1);
      case NAMESPACE:
        return arguments.get(2);
      case SIZE:
        return arguments.get(3);
      case ALIGN:
        return arguments.get(4);
      case STATUS:
        return arguments.get(5);
      case APPEND_TO:
        return arguments.get(6);
      case RETURNORG:
        return arguments.get(7);
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
