package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.IdentifierNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class SNESEmuVectorParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    var body = new DirectiveBodyNode(token);

    while (true) {
      token = parser.getCurrentToken();
      while (token.getType().equals(EOL)) {
        parser.consumeAndClear(TokenTypes.EOL);
        token = parser.getCurrentToken();
      }
      parser.consumeAndClear(TokenTypes.LABEL, TokenTypes.OPCODE);//COP is an opcode as well
      switch (token.getString().toUpperCase()) {
        case "COP":
        case "RESET":
        case "ABORT":
        case "NMI":
        case "IRQBRK": {
          var snesDefinition = new SnesDefinitionNode(token.getString().toUpperCase(), token);
          token = parser.getCurrentToken();
          snesDefinition.setNumericValue(new IdentifierNode(token));
          parser.consumeAndClear(TokenTypes.LABEL);
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

      if (token.getString().toUpperCase().equals(".ENDEMUVECTOR")) {
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
