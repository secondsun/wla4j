package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class StructNode extends DirectiveNode {

  public StructNode(Token token) {
    super(AllDirectives.STRUCT, token, true);
  }

  public String getName() {
    var idNode = (StringExpressionNode) getArguments().getChildren().get(0);
    return idNode.evaluate().trim();
  }
}
