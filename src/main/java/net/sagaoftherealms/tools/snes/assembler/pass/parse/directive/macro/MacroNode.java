package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class MacroNode extends DirectiveNode {

  private final Token startToken;
  private String name;

  public MacroNode(Token startToken) {
    super(AllDirectives.MACRO);
    this.startToken = startToken;
  }

  public Token getStartToken() {
    return startToken;
  }

  @Override
  public void setArguments(DirectiveArgumentsNode arguments) {
    super.setArguments(arguments);
  }

  public String getName() {
    return getArguments().get(0);
  }

}
