package dev.secondsun.wla4j.assembler.pass.parse.directive;

import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

@FunctionalInterface
public interface DirectiveParser {

  default DirectiveBodyNode body(SourceParser parser, Token token) {
    return new DirectiveBodyNode(token);
  }

  DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException;
}
