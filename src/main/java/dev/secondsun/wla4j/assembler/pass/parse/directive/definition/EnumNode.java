package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;

public class EnumNode extends DirectiveNode {

  public EnumNode(Token token) {
    super(AllDirectives.ENUM, token, true);
  }

  public String getAddress() {
    return ((EnumArgumentsNode) getArguments()).get(EnumParser.KEYS.ADDRESS);
  }
}
