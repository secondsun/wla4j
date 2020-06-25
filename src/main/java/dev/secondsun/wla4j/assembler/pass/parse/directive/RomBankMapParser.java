package dev.secondsun.wla4j.assembler.pass.parse.directive;

import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.definition.DefinitionNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.NumericExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

public class RomBankMapParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    var body = new DirectiveBodyNode(token);
    token = parser.getCurrentToken();

    while (true) {
      parser.consume(TokenTypes.LABEL);
      switch (token.getString().toUpperCase()) {
        case "BANKSTOTAL":
        case "BANKSIZE":
        case "BANKS":
          var definition = new DefinitionNode(token.getString(), token);
          body.addChild(definition);

          definition.setSize((NumericExpressionNode) ExpressionParser.expressionNode(parser));
          break;
        default:
          throw new ParseException("Unexpected label", token);
      }
      parser.consumeAndClear(TokenTypes.EOL);

      token = parser.getCurrentToken();
      if (token.getString().toUpperCase().equals(".ENDRO")) {
        parser.consumeAndClear(TokenTypes.DIRECTIVE);
        break;
      }
    }

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException {
    parser.clearWhiteSpaceTokens();
    return new DirectiveArgumentsNode(parser.getCurrentToken());
  }
}
