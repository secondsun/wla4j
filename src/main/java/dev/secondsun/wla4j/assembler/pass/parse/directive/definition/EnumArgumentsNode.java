package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionNode;

public class EnumArgumentsNode extends DirectiveArgumentsNode {

  public EnumArgumentsNode(Token token) {
    super(token);
    addChild(null);
    addChild(null);
    addChild(null);
    // Add three blank arguments
  }

  public void put(EnumParser.KEYS key, ExpressionNode expression) {
    switch (key) {
      case ORDINAL:
        setChildAt(1, expression);
        break;
      case EXPORT:
        setChildAt(2, expression);
        break;
      case ADDRESS:
        setChildAt(0, expression);
        break;
    }
  }

  public void put(EnumParser.KEYS key, String value, Token token) {
    switch (key) {
      case ORDINAL:
        setChildAt(1, new StringExpressionNode(value, token));
        break;
      case EXPORT:
        setChildAt(2, new StringExpressionNode(value, token));
        break;
      case ADDRESS:
        setChildAt(0, new StringExpressionNode(value, token));
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
