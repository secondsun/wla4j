package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

public class StructNode extends DirectiveNode {

  public StructNode() {
    super(AllDirectives.STRUCT);
  }

  public String getName() {
    return getArguments().get(0).trim();
  }
}
