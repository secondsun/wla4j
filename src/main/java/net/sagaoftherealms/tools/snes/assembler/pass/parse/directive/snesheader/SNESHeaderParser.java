package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class SNESHeaderParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    var body = new DirectiveBodyNode(token);

    while (true) {
      token = parser.getCurrentToken();
      while (token.getType().equals(EOL)) {
        parser.consumeAndClear(TokenTypes.EOL);
        token = parser.getCurrentToken();
      }
      parser.consumeAndClear(TokenTypes.LABEL);
      switch (token.getString().toUpperCase()) {
        case "ID": {
          var snesDefinition = new SnesDefinitionNode("ID", token);
          token = parser.getCurrentToken();
          snesDefinition.setName(token.getString(), token);
          parser.consumeAndClear(TokenTypes.STRING);
          body.addChild(snesDefinition);
          break;
        }
        case "NAME": {
          var snesDefinition = new SnesDefinitionNode("NAME", token);
          token = parser.getCurrentToken();
          snesDefinition.setName(token.getString(), token);
          parser.consumeAndClear(TokenTypes.STRING);
          body.addChild(snesDefinition);
          break;
        }
        case "HIROM":
        case "EXHIROM":
        case "LOROM":
        case "SLOWROM":
        case "FASTROM": {
          var snesDefinition = new SnesDefinitionNode(token.getString().toUpperCase(), token);
          body.addChild(snesDefinition);
          break;
        }
        case "CARTRIDGETYPE":
        case "ROMSIZE":
        case "SRAMSIZE":
        case "COUNTRY":
        case "LICENSEECODE":
        case "VERSION":
        {
          var snesDefinition = new SnesDefinitionNode(token.getString().toUpperCase(), token);
          snesDefinition.setSize((NumericExpressionNode) ExpressionParser.expressionNode(parser));
          body.addChild(snesDefinition);
          break;
        }
        default:
          throw new ParseException("Unexpected label", token);
      }

      token = parser.getCurrentToken();
      while (token.getType().equals(EOL)) {
        parser.consumeAndClear(TokenTypes.EOL);
        token = parser.getCurrentToken();
      }

      if (token.getString().toUpperCase().equals(".ENDSNES")) {
        parser.consumeAndClear(TokenTypes.DIRECTIVE);
        break;
      }
    }

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException {
    return new DirectiveArgumentsNode(parser.getCurrentToken());
  }
}
