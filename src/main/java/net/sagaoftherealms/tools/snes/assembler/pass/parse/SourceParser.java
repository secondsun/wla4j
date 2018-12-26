package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.COMMA;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveUtils;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;

public class SourceParser {

  private final SourceScanner scanner;
  private Token token;
  private Map<String, MacroNode> macroMap = new HashMap<>();

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
        consume(TokenTypes.STRING);
        return new StringExpressionNode(token.getString());
      case DIRECTIVE:
        var directiveName = token.getString();
        var directiveNode = directive(directiveName);
        clearWhiteSpaceTokens();
        return directiveNode;
      case NUMBER:
        return ExpressionParser.expressionNode(this);
      case LABEL:
        if (macroMap.containsKey(token.getString())) {
          MacroCallNode macroCall = macroCall();
          clearWhiteSpaceTokens();
          return macroCall;
        }
      case MINUS:
      case PLUS:
        var definition =  new LabelDefinitionNode(token);
        consume(token.getType());
        return definition;
      case LEFT_PAREN:
        var node = ExpressionParser.expressionNode(this);
        return node;
      case COMMA:
        consume(TokenTypes.COMMA);
        return nextNode();
      case OPCODE:
        OpcodeNode opcodeNode = opcode();
        return opcodeNode;
      case EOL:
        consume(TokenTypes.EOL);
        return nextNode();
    }
    return null;
  }

  private MacroCallNode macroCall() {
    var node = new MacroCallNode(macroMap.get(token.getString()));
    consume(LABEL);
    while (!token.getType().equals(EOL) && !token.getType().equals(END_OF_INPUT)) {
      if (!token.getType().equals(TokenTypes.COMMA)) {
        node.addArgument(ExpressionParser.expressionNode(this));
      } else {
        consume(COMMA);
      }
    }
    consume(EOL, END_OF_INPUT);

    return node;
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

  /**
   * Confirms that the current token is expected and advances to the next token.
   */
  public void consume(TokenTypes... types) {
    final var typesList = Arrays.asList(types);
    if (typesList.contains(token.getType())) {
      advanceToken();
    } else {
      throw new ParseException(
          "Unexpected Token.  One of " + typesList + " was expected.",
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

    if (node instanceof MacroNode) {
      var macroNode = (MacroNode) node;
      macroMap.put(macroNode.getName(), macroNode);
    }

    return node;
  }

  /**
   * Move the token past any whitespace / comments
   */
  public void clearWhiteSpaceTokens() {
    var token = getCurrentToken();

    while (token != null && EOL.equals(token.getType())) {
      consume(TokenTypes.EOL);
      token = getCurrentToken();
    }
  }

  public Token peekNextToken() {
    return scanner.peekNextToken();
  }
}
