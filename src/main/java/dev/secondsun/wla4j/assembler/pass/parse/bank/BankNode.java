package dev.secondsun.wla4j.assembler.pass.parse.bank;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class BankNode extends DirectiveNode {

  public BankNode(Token token) {
    super(AllDirectives.BANK, token, false);
  }
}
