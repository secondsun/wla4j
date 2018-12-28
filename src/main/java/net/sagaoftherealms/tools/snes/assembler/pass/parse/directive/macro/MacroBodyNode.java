package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class MacroBodyNode extends Node {

  public MacroBodyNode(Token token) {
    super(NodeTypes.MACRO_BODY, token);
  }
}
