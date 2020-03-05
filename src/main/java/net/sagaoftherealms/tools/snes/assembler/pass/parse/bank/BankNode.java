package net.sagaoftherealms.tools.snes.assembler.pass.parse.bank;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class BankNode extends DirectiveNode {

  public BankNode(Token token) {
    super(AllDirectives.BANK, token, false);
  }
}
