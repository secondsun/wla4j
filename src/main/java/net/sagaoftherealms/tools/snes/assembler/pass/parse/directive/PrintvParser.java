package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class PrintvParser extends GenericDirectiveParser {

  public PrintvParser() {
    super(AllDirectives.PRINTV);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var node = new DirectiveArgumentsNode(token);

    if (token.getString().equalsIgnoreCase("hex")) {
      node.add(new StringExpressionNode("hex", token));
      parser.consume(token.getType());
    } else if (token.getString().equalsIgnoreCase("dec")) {
      node.add(new StringExpressionNode("dec", token));
      parser.consume(token.getType());
    } else {
      node.add(new StringExpressionNode("dec", token));
    }

    node.add(ExpressionParser.expressionNode(parser));

    parser.consume(TokenTypes.EOL, TokenTypes.END_OF_INPUT);

    return node;
  }
}
