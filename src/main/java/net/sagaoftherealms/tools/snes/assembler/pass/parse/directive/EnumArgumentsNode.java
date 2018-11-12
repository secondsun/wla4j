package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

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
        arguments.set(1, value);
        break;
      case EXPORT:
        arguments.set(2, value);
        break;
      case ADDRESS:
        arguments.set(0, value);
        break;
    }
  }

  public String get(EnumParser.KEYS key) {
    switch (key) {
      case ORDINAL:
        return arguments.get(1);
      case EXPORT:
        return arguments.get(2);
      case ADDRESS:
        return arguments.get(0);
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
