package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;

@FunctionalInterface
public interface DirectiveParser {


  default DirectiveBodyNode body(SourceParser parser) {
    return new DirectiveBodyNode();
  }

  DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException;
}
