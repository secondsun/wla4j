package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.IdentifierNode;
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
    var token = parser.getCurrentToken();
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode(token);

    if (token.getType().equals(TokenTypes.LABEL)) {
      arguments.add(new IdentifierNode(token));
      parser.consume(TokenTypes.LABEL);
    } else {
      int times = TokenUtil.getInt(token);
      arguments.add(new ConstantNode(times, token));
      parser.consume(TokenTypes.NUMBER);
    }
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
