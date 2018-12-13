package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes.DIRECTIVE_BODY;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;


public class DirectiveBodyNode extends Node {

  public DirectiveBodyNode() {
    super(DIRECTIVE_BODY);
  }
}
