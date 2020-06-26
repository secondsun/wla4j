package dev.secondsun.wla4j.assembler.pass.parse.directive.gbheader;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveUtils;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

public class GBHeaderParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    DirectiveBodyNode body = new DirectiveBodyNode(token);
    parser.clearWhiteSpaceTokens();
    token = parser.getCurrentToken();
    while (token.getType().equals(TokenTypes.LABEL)
        || token.getType().equals(TokenTypes.DIRECTIVE)) {
      switch (token.getType()) {
        case LABEL:
          // convert to directive
          parser.consume(TokenTypes.LABEL);
          var transformedToken =
              new Token(
                  "." + token.getString().toUpperCase(),
                  TokenTypes.DIRECTIVE,
                  token.getFileName(),
                  token.getPosition());
          var dParser = DirectiveUtils.getParser(AllDirectives.valueOf(token.getString()));
          DirectiveNode node =
              DirectiveUtils.createDirectiveNode(transformedToken.getString(), token);
          node.setArguments(dParser.arguments(parser));
          body.addChild(node);
          break;
        case DIRECTIVE:
          if (token.getString().toUpperCase().matches(".ENDGB")) {
            parser.consume(TokenTypes.DIRECTIVE);
            break;
          }
          body.addChild(parser.nextNode());
          break;
        default:
          throw new ParseException("This should not happen", token);
      }
      parser.clearWhiteSpaceTokens();
      token = parser.getCurrentToken();
    }

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException {
    return new DirectiveArgumentsNode(parser.getCurrentToken());
  }
}
