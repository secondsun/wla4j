package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;

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
