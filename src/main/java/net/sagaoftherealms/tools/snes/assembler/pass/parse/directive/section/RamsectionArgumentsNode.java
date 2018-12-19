package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;

public class RamsectionArgumentsNode extends DirectiveArgumentsNode {
  public enum RamsectionArguments {
    BANK,NAME,SLOT,APPEND_TO, ALIGN
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
        arguments.set(0, value);
        break;
      case BANK:
        arguments.set(1, value);
        break;
      case SLOT:
        arguments.set(2, value);
        break;

      case ALIGN:
        arguments.set(3, value);
        break;

      case APPEND_TO:
        arguments.set(4, value);
        break;

    }
  }

  public String get(RamsectionArguments key) {
    switch (key) {
      case NAME:
        return arguments.get(0);
      case BANK:
        return arguments.get(1);
      case SLOT:
        return arguments.get(2);

      case ALIGN:
        return arguments.get(3);

      case APPEND_TO:
        return arguments.get(4);

    }
    throw new IllegalArgumentException("Unknown Key");
  }

}
