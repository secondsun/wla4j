package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class IdentifierNode extends NumericExpressionNode {

  private final Token token;

  public IdentifierNode(Token token) {
    super(NodeTypes.IDENTIFIER_EXPRESSION);
    this.token = token;
  }

  public String getLabelName() {
    return TokenUtil.getLabelName(token);
  }
}
