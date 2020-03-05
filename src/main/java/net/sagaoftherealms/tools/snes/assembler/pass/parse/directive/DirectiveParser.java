package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

@FunctionalInterface
public interface DirectiveParser {

  default DirectiveBodyNode body(SourceParser parser, Token token) {
    return new DirectiveBodyNode(token);
  }

  DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException;
}
