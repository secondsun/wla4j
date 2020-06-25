package dev.secondsun.wla4j.assembler.pass.parse.directive.incbin;

import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class IncBinArgumentsNode extends DirectiveArgumentsNode {
  public enum Arguments {
    NAME,
    SKIP,
    READ,
    SWAP,
    FSIZE,
    FILTER
  };

  public IncBinArgumentsNode(Token token) {
    super(token);
    addChild(null);
    addChild(null);
    addChild(null);
    addChild(null);
    addChild(null);
    addChild(null);
  }

  public void put(Arguments key, ExpressionNode value) {
    setChildAt(key.ordinal(), value);
  }

  public ExpressionNode get(Arguments key) {
    return (ExpressionNode) getChildAt(key.ordinal());
  }
}
