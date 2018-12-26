package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class RepeatParser extends GenericDirectiveParser {

  public RepeatParser(AllDirectives type) {
    super(type);
    if (!(type.equals(AllDirectives.REPT) || type.equals(AllDirectives.REPEAT))) {
      throw new IllegalArgumentException(type + " not expected");
    }
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode();
    var token = parser.getCurrentToken();

    int times = TokenUtil.getInt(token);
    arguments.add(new ConstantNode(times));
    parser.consume(TokenTypes.NUMBER);

    token = parser.getCurrentToken();
    if (token.getString().equalsIgnoreCase("index")) {
      parser.consume(TokenTypes.LABEL);

      token = parser.getCurrentToken();
      parser.consume(TokenTypes.LABEL);

      arguments.add(new IdentifierNode(token));
    }

    return arguments;
  }
}
