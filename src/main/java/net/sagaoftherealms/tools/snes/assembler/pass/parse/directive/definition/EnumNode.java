package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class EnumNode extends DirectiveNode {

  public EnumNode(Token token) {
    super(AllDirectives.ENUM, token);
  }

  public String getAddress() {
    return ((EnumArgumentsNode) getArguments()).get(EnumParser.KEYS.ADDRESS);
  }
}
