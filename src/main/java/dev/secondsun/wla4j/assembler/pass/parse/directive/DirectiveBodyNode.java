package dev.secondsun.wla4j.assembler.pass.parse.directive;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class DirectiveBodyNode extends Node {

  public DirectiveBodyNode(Token token) {
    super(NodeTypes.DIRECTIVE_BODY, token);
  }
}
