package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;

public class IdentifierNode extends NumericExpressionNode {

  private final Token token;

  public IdentifierNode(Token token) {
    super(NodeTypes.IDENTIFIER_EXPRESSION, token);
    this.token = token;
  }

  public String getLabelName() {
    return TokenUtil.getLabelName(token);
  }
}
