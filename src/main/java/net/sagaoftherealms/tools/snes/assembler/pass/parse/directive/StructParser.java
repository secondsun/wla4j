package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;

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
