package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.factory.DirectiveUtils;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;

public class SourceParser {

  private final SourceScanner scanner;
  Token token;

  public SourceParser(SourceScanner scanner) {
    this.scanner = scanner;
  }

  public Node nextNode() {
    advanceToken();
    switch (token.getType()) {
      case STRING:
        break;
      case DIRECTIVE:
        return directive();
      case NUMBER:
        break;
      case LABEL:
        break;
      case PLUS:
        break;
      case MINUS:
        break;
      case LT:
        break;
      case GT:
        break;
      case LEFT_BRACKET:
        break;
      case RIGHT_BRACKET:
        break;
      case LEFT_PAREN:
        break;
      case RIGHT_PAREN:
        break;
      case COMMA:
        break;
      case OR:
        break;
      case AND:
        break;
      case POWER:
        break;
      case MODULO:
        break;
      case MULTIPLY:
        break;
      case DIVIDE:
        break;
      case XOR:
        break;
      case OPCODE:
        break;
      case SIZE:
        break;
      case EOL:
        return nextNode();
    }
    return null;
  }

  public void advanceToken() {
    this.token = scanner.getNextToken();
  }

  public Token getCurrentToken() {
    return this.token;
  }

  private Node directive() {
    var directiveName = token.getString();
    advanceToken(); // Clear the directive Name token

    var node = DirectiveUtils.createDirectiveNode(directiveName);
    var nodeParser = DirectiveUtils.getParser(node.getDirectiveType());
    node.setArguments(nodeParser.arguments(this));
    node.setBody(nodeParser.body(this));
    return node;
  }

  private Node body(NodeTypes nodeType) {
    return null;
  }

  private Node arguments(NodeTypes nodeType) {
    return null;
  }
}
