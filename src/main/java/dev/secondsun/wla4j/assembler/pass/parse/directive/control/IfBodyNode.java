package dev.secondsun.wla4j.assembler.pass.parse.directive.control;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class IfBodyNode extends DirectiveBodyNode {

  public IfBodyNode(Node thenBody, Node elseBody, Token token) {
    super(token);
    this.addChild(thenBody);
    this.addChild(elseBody);
  }

  public Node getThenBody() {
    return this.getChildren().get(0);
  }

  public Node getElseBody() {
    return this.getChildren().get(1);
  }
}
