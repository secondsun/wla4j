package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class PrintvParser extends GenericDirectiveParser {

  public PrintvParser() {
    super(AllDirectives.PRINTV);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException {
    var node = new DirectiveArgumentsNode();

    var token = parser.getCurrentToken();
    if (token.getString().equalsIgnoreCase("hex")) {
      node.add("hex");
      parser.consume(token.getType());
    } else if (token.getString().equalsIgnoreCase("dec")) {
      node.add("dec");
      parser.consume(token.getType());
    } else {
      node.add("dec");
    }

    node.add(ExpressionParser.expressionNode(parser));

    parser.consume(TokenTypes.EOL, TokenTypes.END_OF_INPUT);

    return node;
  }
}
