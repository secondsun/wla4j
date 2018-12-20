package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MINUS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.PLUS;

import java.util.Arrays;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveUtils;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;

public class SourceParser {

  private final SourceScanner scanner;
  private Token token;

  public SourceParser(SourceScanner scanner) {
    this.scanner = scanner;
    token = scanner.getNextToken();
  }

  /**
   * This will create a node from the location of the parser in the source scanner. This will move
   * the current token, has side effects, etc. Needless to say it is not thread safe.
   *
   * @return the nextNode that the current token is on.
   */
  public Node nextNode() {
    switch (token.getType()) {
      case STRING:
        break;
      case DIRECTIVE:
        var directiveName = token.getString();
        var directiveNode = directive(directiveName);
        clearWhiteSpaceTokens();
        return directiveNode;
      case NUMBER:
        break;
      case LABEL:
        {
          var labelNode = new LabelNode(token);
          consumeAndClear(TokenTypes.LABEL);
          return labelNode;
        }
      case MINUS:
      case PLUS:
        {
          LabelNode labelNode = createUnnamedLabel(token);
          return labelNode;
        }
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
        OpcodeNode opcodeNode = opcode();
        return opcodeNode;
      case SIZE:
        break;
      case EOL:
        consume(TokenTypes.EOL);
        return nextNode();
    }
    return null;
  }

  private OpcodeNode opcode() {
    var opcode = new OpcodeNode(token);
    consume(TokenTypes.OPCODE);
    token = getCurrentToken();

    while (!token.getType().equals(EOL) && !token.getType().equals(END_OF_INPUT)) {
      opcode.addChild(new OpcodeArgumentNode(getCurrentToken()));
      consume(getCurrentToken().getType());
    }

    consumeAndClear(EOL, END_OF_INPUT);
    return opcode;
  }

  private LabelNode createUnnamedLabel(Token token) {
    StringBuilder labelNameBuilder = new StringBuilder(10);
    Token initialToken = token;

    while (PLUS.equals(token.getType()) || MINUS.equals(token.getType())) {
      switch (token.getType()) {
        case PLUS:
          labelNameBuilder.append('+');
          consumeAndClear(TokenTypes.PLUS);
          token = getCurrentToken();
          break;
        case MINUS:
          labelNameBuilder.append('-');
          consumeAndClear(TokenTypes.MINUS);
          token = getCurrentToken();
          break;
        default:
          throw new ParseException("expected +* or -*.", token);
      }
    }
    var label = new LabelNode(labelNameBuilder.toString(), token);
    return label;
  }

  /** Confirms that the current token is expected and advances to the next token. */
  public void consume(TokenTypes... types) {
    final var typesList = Arrays.asList(types);
    if (typesList.contains(token.getType())) {
      advanceToken();
    } else {
      throw new ParseException(
          "Unexpected Type " + token + ".  One of " + typesList + " was expected.",
          getCurrentToken());
    }
  }

  public void consumeAndClear(TokenTypes... types) {
    consume(types);
    clearWhiteSpaceTokens();
  }

  private void advanceToken() {
    this.token = scanner.getNextToken();
  }

  /**
   * The token that the parser is ready to consume. Calling nextNode will change this value. If you
   * need the token of a node, call this before you call next node.
   *
   * @return the current token under the parser.
   */
  public Token getCurrentToken() {
    return this.token;
  }

  private Node directive(String directiveName) {

    consume(TokenTypes.DIRECTIVE);

    var node = DirectiveUtils.createDirectiveNode(directiveName, getCurrentToken());
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

  /** Move the token past any whitespace / comments */
  public void clearWhiteSpaceTokens() {
    var token = getCurrentToken();

    while (token != null && EOL.equals(token.getType())) {
      consume(TokenTypes.EOL);
      token = getCurrentToken();
    }
  }
}
