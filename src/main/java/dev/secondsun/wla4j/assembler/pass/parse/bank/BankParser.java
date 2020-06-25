package dev.secondsun.wla4j.assembler.pass.parse.bank;

import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.EOL;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

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
