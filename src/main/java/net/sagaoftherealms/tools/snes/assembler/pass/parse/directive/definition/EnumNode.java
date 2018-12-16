package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

public class EnumNode extends DirectiveNode {

  public EnumNode() {
    super(AllDirectives.ENUM);
  }

  public String getAddress() {
    return ((EnumArgumentsNode) getArguments()).get(EnumParser.KEYS.ADDRESS);
  }
}
