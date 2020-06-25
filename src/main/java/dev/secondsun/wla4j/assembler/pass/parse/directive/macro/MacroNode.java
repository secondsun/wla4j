package dev.secondsun.wla4j.assembler.pass.parse.directive.macro;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class MacroNode extends DirectiveNode {

  private final Token startToken;

  public MacroNode(Token startToken) {
    super(AllDirectives.MACRO, startToken, true);
    this.startToken = startToken;
  }

  public Token getStartToken() {
    return startToken;
  }

  @Override
  public String toString() {
    return "MacroNode{" + "name=" + getName() + '}';
  }

  @Override
  public void setArguments(DirectiveArgumentsNode arguments) {
    super.setArguments(arguments);
  }

  public String getName() {
    return getArguments().getString(0);
  }
}
