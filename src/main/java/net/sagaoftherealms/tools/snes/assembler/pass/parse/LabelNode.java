package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class LabelNode extends Node {

  private final Token token;
  private final String labelName;

  public LabelNode(Token token) {
    super(NodeTypes.LABEL);
    this.token = token;
    this.labelName = TokenUtil.getLabelName(token);
  }

  public LabelNode(String labelName, Token token) {
    super(NodeTypes.LABEL);
    this.token = token;
    this.labelName = labelName;
  }

  public String getLabelName() {
    return labelName;
  }
}
