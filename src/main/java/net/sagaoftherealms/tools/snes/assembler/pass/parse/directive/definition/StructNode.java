package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;

public class StructNode extends DirectiveNode {

  public StructNode() {
    super(AllDirectives.STRUCT);
  }

  public String getName() {
    var idNode = (StringExpressionNode) getArguments().getChildren().get(0);
    return idNode.evaluate().trim();
  }
}
