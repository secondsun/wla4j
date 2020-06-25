package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;

/** This class parses Structs */
public class StructParser extends BodyDefinitionParser {

  public StructParser() {
    super(AllDirectives.STRUCT);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    return super.arguments(parser);
  }
}
