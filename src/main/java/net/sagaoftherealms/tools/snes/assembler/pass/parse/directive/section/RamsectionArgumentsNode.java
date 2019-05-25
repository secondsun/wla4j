package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class RamsectionArgumentsNode extends DirectiveArgumentsNode {
  public enum RamsectionArguments {
    BANK,
    NAME,
    SLOT,
    APPEND_TO,
    ALIGN
  }

  public RamsectionArgumentsNode(Token token) {
    super(token);
    addChild(null);
    addChild(null);
    addChild(null);
    addChild(null);
    addChild(null);
  }

  public void put(RamsectionArguments key, String value, Token token) {
    switch (key) {
      case NAME:
        setChildAt(0, new StringExpressionNode(value, token));
        break;
      case BANK:
        setChildAt(1, new StringExpressionNode(value, token));
        break;
      case SLOT:
        setChildAt(2, new StringExpressionNode(value, token));
        break;

      case ALIGN:
        setChildAt(3, new StringExpressionNode(value, token));
        break;

      case APPEND_TO:
        setChildAt(4, new StringExpressionNode(value, token));
        break;
    }
  }

  public String get(RamsectionArguments key) {
    switch (key) {
      case NAME:
        return safeGet(0);
      case BANK:
        return safeGet(1);
      case SLOT:
        return safeGet(2);

      case ALIGN:
        return safeGet(3);

      case APPEND_TO:
        return safeGet(4);
    }
    throw new IllegalArgumentException("Unknown Key");
  }
}
