package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes.DIRECTIVE_BODY;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class DirectiveBodyNode extends Node {

  public DirectiveBodyNode(Token token) {
    super(DIRECTIVE_BODY, token);
  }
}
