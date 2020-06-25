package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;

/** This class parses Unions */
public class UnionParser extends BodyDefinitionParser {

  public UnionParser() {
    super(AllDirectives.UNION);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    return super.arguments(parser);
  }
}
