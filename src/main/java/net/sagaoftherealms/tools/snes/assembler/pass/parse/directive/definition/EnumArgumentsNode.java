package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class EnumArgumentsNode extends DirectiveArgumentsNode {

  public EnumArgumentsNode(Token token) {
    super(token);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    // Add three blank arguments
  }

  public void put(EnumParser.KEYS key, String value, Token token) {
    switch (key) {
      case ORDINAL:
        arguments.set(1, new StringExpressionNode(value,token));
        break;
      case EXPORT:
        arguments.set(2, new StringExpressionNode(value,token));
        break;
      case ADDRESS:
        arguments.set(0, new StringExpressionNode(value,token));
        break;
    }
  }

  public String get(EnumParser.KEYS key) {
    switch (key) {
      case ORDINAL:
        return safeGet(1);
      case EXPORT:
        return safeGet(2);
      case ADDRESS:
        return safeGet(0);
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
