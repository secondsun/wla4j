package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import java.util.Optional;

public class UnionNode extends DirectiveNode {

  Optional<UnionNode> nextUnion = Optional.empty();

  public UnionNode(Token token) {
    super(AllDirectives.UNION, token, true);
  }

  public String getName() {
    var idNode = (StringExpressionNode) getArguments().getChildren().get(0);
    return idNode.evaluate().trim();
  }

  public Optional<UnionNode> nextUnion() {
    return nextUnion;
  }

  public void setNextUnion(UnionNode next) {
    nextUnion = Optional.of(next);
  }
}
