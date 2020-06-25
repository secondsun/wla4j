package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class StructNode extends DirectiveNode {

  public StructNode(Token token) {
    super(AllDirectives.STRUCT, token, true);
  }

  public String getName() {
    var idNode = (StringExpressionNode) getArguments().getChildren().get(0);
    return idNode.evaluate().trim();
  }
}
