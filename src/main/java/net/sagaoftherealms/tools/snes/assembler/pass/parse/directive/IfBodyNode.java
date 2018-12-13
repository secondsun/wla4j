package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;

public class IfBodyNode extends DirectiveBodyNode {

  public IfBodyNode(Node thenBody,
      Node elseBody) {
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
