package dev.secondsun.wla4j.assembler.pass.parse.directive.macro;

import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

public class MacroParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    DirectiveBodyNode body = new DirectiveBodyNode(token);
    token = parser.getCurrentToken();

    while (!token.getType().equals(TokenTypes.END_OF_INPUT)) {
      if (token.getString().equalsIgnoreCase(".endm")) {
        break;
      }

      body.addChild(new MacroBodyNode(token));

      parser.consume(token.getType());
      token = parser.getCurrentToken();
    }

    parser.consumeAndClear(TokenTypes.DIRECTIVE);

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var node = new DirectiveArgumentsNode(token);

    var nodeName = token.getString();
    node.add(new StringExpressionNode(nodeName, token));
    parser.consume(TokenTypes.LABEL);
    token = parser.getCurrentToken();

    if (token.getString().equalsIgnoreCase("ARGS")) {
      parser.consume(TokenTypes.LABEL);
      token = parser.getCurrentToken();
      while (!token.getType().equals(TokenTypes.EOL)) {
        node.add(new StringExpressionNode(token.getString(), token));
        parser.consume(TokenTypes.LABEL);
        token = parser.getCurrentToken();
        if (!token.getType().equals(TokenTypes.EOL)) {
          parser.consume(TokenTypes.COMMA);
          token = parser.getCurrentToken();
        }
      }
    }
    parser.consume(TokenTypes.EOL);
    return node;
  }
}
