package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;

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
