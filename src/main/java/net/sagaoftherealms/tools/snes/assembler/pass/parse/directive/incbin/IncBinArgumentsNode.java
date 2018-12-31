package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.incbin;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

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
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
    arguments.add(null);
  }

  public void put(Arguments key, ExpressionNode value) {
    arguments.set(key.ordinal(), value);
  }

  public ExpressionNode get(Arguments key) {
    return arguments.get(key.ordinal());
  }
}