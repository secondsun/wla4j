package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SlotNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class MemoryMapParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    var body = new DirectiveBodyNode(token);
    token = parser.getCurrentToken();

    while(true) {
      parser.consume(TokenTypes.LABEL);
      switch(token.getString().toUpperCase()) {
        case "SLOTSIZE":
        case "DEFAULTSLOT":
          var definition = new DefinitionNode(token.getString(), token);
          body.addChild(definition);
          definition.setSize((NumericExpressionNode) ExpressionParser.expressionNode(parser));
          break;
        case "SLOT":
          
          var node = new SlotNode(token);
          body.addChild(node);
          token = parser.getCurrentToken();
          node.setNumber(TokenUtil.getInt(token));

          parser.consume(TokenTypes.NUMBER);
          token = parser.getCurrentToken();
          
          if (token.getString().equalsIgnoreCase("START")) {
            parser.consume(TokenTypes.LABEL);
            token = parser.getCurrentToken();
          }
          
          node.setStart(TokenUtil.getInt(token));

          parser.consume(TokenTypes.NUMBER);
          token = parser.getCurrentToken();

          if (token.getString().equalsIgnoreCase("SIZE")) {
            parser.consume(TokenTypes.LABEL);
            token = parser.getCurrentToken();
          }
          
          if (EOL.equals(token.getType())) {
            break;
          }
          
          token = parser.getCurrentToken();
          node.setSize(TokenUtil.getInt(token));

          parser.consume(TokenTypes.NUMBER);
          parser.consume(EOL);

          break;
        default:
          throw new ParseException("Unexpected label", token);
      }
      parser.consumeAndClear(EOL);

      token = parser.getCurrentToken();
      if (token.getString().toUpperCase().equals(".ENDME")  ) {
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
