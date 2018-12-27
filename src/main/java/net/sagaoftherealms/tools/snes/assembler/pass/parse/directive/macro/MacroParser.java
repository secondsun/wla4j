package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class MacroParser implements DirectiveParser {

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    DirectiveBodyNode body = new DirectiveBodyNode();
    var node = parser.nextNode();
    while (node.getType() != NodeTypes.DIRECTIVE
        || (((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDM)) {
      body.addChild(node);
      node = parser.nextNode();

      if (node == null) {
        throw new ParseException("Unexpected end of file", parser.getCurrentToken());
      }
    }

    if (((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDM) {
      throw new IllegalStateException("Expected End");
    }

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var node = new DirectiveArgumentsNode();
    var token = parser.getCurrentToken();

    var nodeName = token.getString();
    node.add(nodeName);
    parser.consume(TokenTypes.LABEL);
    token = parser.getCurrentToken();

    if (token.getString().equalsIgnoreCase("ARGS")) {
      parser.consume(TokenTypes.LABEL);
      token = parser.getCurrentToken();
      while (!token.getType().equals(TokenTypes.EOL)) {
        node.add(token.getString());
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
