package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;

public class LabelDefinitionNode extends Node {

  private final Token token;
  private final String labelName;

  public LabelDefinitionNode(Token token) {
    super(NodeTypes.LABEL_DEFINITION, token);
    this.token = token;
    this.labelName = TokenUtil.getLabelName(token);
  }

  public LabelDefinitionNode(String labelName, Token token) {
    super(NodeTypes.LABEL, token);
    this.token = token;
    this.labelName = labelName;
  }

  public String getLabelName() {
    return labelName;
  }
}
