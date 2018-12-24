package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class IfDefForMacrosParser extends IfParser {

  public IfDefForMacrosParser(AllDirectives type) {
    super(type);
    if (!(type == AllDirectives.IFDEFM || type == AllDirectives.IFNDEFM)) {
      throw new IllegalArgumentException("Invalid Type." + type);
    }
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var node = new DirectiveArgumentsNode();

    var label = parser.getCurrentToken();

    if (!label.getString().matches("\\\\\\d+")) {
      throw new ParseException("Macro labels should be in the form \\d+", label);
    }

    parser.consume(TokenTypes.LABEL);
    parser.consume(TokenTypes.EOL);

    node.add(label.toString());

    return node;
  }
}
