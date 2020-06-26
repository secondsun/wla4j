package dev.secondsun.wla4j.assembler.pass.parse.directive;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class DirectiveNode extends Node {

  private final AllDirectives directive;
  private final boolean hasBody;

  public DirectiveNode(AllDirectives directive, Token token, boolean hasBody) {

    super(DirectiveUtils.getDirectiveNodeType(directive), token);
    this.directive = directive;
    this.hasBody = hasBody;
  }

  public DirectiveArgumentsNode getArguments() {
    if (getChildren() == null || getChildren().isEmpty()) {
      return new DirectiveArgumentsNode(getSourceToken());
    }
    return (DirectiveArgumentsNode)
        getChildren().get(0); // TODO throw exception if missing children?
  }

  public void setArguments(DirectiveArgumentsNode arguments) {
    if (getChildren().size() != 0) {
      throw new IllegalStateException(
          "Must set arguments only once and before you set any other children nodes.");
    }
    addChild(arguments);
  }

  public DirectiveBodyNode getBody() {
    if (getChildren().size() > 1) {
      return (DirectiveBodyNode) getChildren().get(1);
    } else {
      return null; // TODO maybe throw an exception instead.
    }
  }

  public void setBody(DirectiveBodyNode body) {
    if (getChildren().size() != 1) {
      throw new IllegalStateException("Must set body only once only after you set arguments.");
    }
    addChild(body);
  }

  public AllDirectives getDirectiveType() {
    return directive;
  }

  public boolean hasBody() {
    return hasBody;
  }

  @Override
  public String toString() {
    return directive + "(" + getArguments().toString() + ");";
  }
}
