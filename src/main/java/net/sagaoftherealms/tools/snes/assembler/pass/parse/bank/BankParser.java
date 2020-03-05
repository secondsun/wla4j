package net.sagaoftherealms.tools.snes.assembler.pass.parse.bank;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class BankParser extends GenericDirectiveParser {

  public BankParser() {
    super(AllDirectives.BANK);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var node = new DirectiveArgumentsNode(parser.getCurrentToken());

    node.add(ExpressionParser.expressionNode(parser));

    var token = parser.getCurrentToken();
    if (token.getString().equalsIgnoreCase("slot")) {
      parser.consume(TokenTypes.LABEL);
      node.add(ExpressionParser.expressionNode(parser));
    }

    parser.consume(EOL, END_OF_INPUT);

    return node;
  }
}
