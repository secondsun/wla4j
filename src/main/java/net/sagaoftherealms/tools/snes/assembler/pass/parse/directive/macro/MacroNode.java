package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class MacroNode extends DirectiveNode {

  private final Token startToken;

  public MacroNode(Token startToken) {
    super(AllDirectives.MACRO, startToken);
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
