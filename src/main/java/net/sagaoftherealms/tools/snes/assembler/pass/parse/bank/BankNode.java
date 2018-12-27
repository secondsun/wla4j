package net.sagaoftherealms.tools.snes.assembler.pass.parse.bank;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

public class BankNode extends DirectiveNode {

  public BankNode() {
    super(AllDirectives.BANK);
  }
}
