package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveUtils;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.macro.MacroNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.parse.visitor.Visitor;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.util.SourceScanner;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceParser {

  private static final Logger LOG = Logger.getLogger("SourceParser");

  private final SourceScanner scanner;
  private Token token;
  private Map<String, Optional<MacroNode>> macroMap = new HashMap<>();
  private Set<String> includes = new HashSet<>();
  private List<ErrorNode> errors = new ArrayList<>();
  private List<Visitor> visitors = new ArrayList<>();

  public SourceParser(SourceScanner scanner) {
    this.scanner = scanner;
    token = scanner.getNextToken();
  }

  public SourceParser(SourceScanner scanner, Map<String, Optional<MacroNode>> macroMap) {
    this.macroMap.putAll(macroMap);
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
    try {
      Node node = null;
      switch (token.getType()) {
        case STRING:
          var stringExpression = new StringExpressionNode(token.getString(), token);
          consume(TokenTypes.STRING);
          node = stringExpression;
          break;
        case DIRECTIVE:
          var directiveName = token.getString();
          var directiveNode = directive(directiveName);
          clearWhiteSpaceTokens();
          node = directiveNode;
          break;
        case NUMBER:
          node = ExpressionParser.expressionNode(this);
          break;
        case LABEL:
          if (macroMap.containsKey(token.getString())) {
            MacroCallNode macroCall = macroCall();
            clearWhiteSpaceTokens();
            node = macroCall;
          } else {
            var definition = new LabelDefinitionNode(token);
            consume(token.getType());
            node = definition;
          }
          break;
        case MINUS:
        case PLUS:
          var definition = new LabelDefinitionNode(token);
          consume(token.getType());
          node = definition;
          break;
        case LEFT_PAREN:
          node = ExpressionParser.expressionNode(this);
          break;
        case COMMA:
          consume(TokenTypes.COMMA);
          node = nextNode();
          break;
        case OPCODE:
          OpcodeNode opcodeNode = opcode();
          node = opcodeNode;
          break;
        case ERROR:
          var problemToken = token;
          consume(TokenTypes.ERROR);
          throw new ParseException("Invalid token", problemToken);
        case EOL:
          consume(TokenTypes.EOL);
          node = nextNode();
          break;
        default:
          node = null;
          break;
      }
      if (node != null) {
        applyVisitors(node);
      }
      return node;
    } catch (ParseException e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      var node = new ErrorNode(token, e);
      errors.add(node);
      return node;
    }
  }

  private void applyVisitors(Node node) {
    visitors.forEach(node::accept);
  }

  private MacroCallNode macroCall() {
    var node = new MacroCallNode(token.getString(), token);
    consume(TokenTypes.LABEL);
    while (!token.getType().equals(TokenTypes.EOL)
        && !token.getType().equals(TokenTypes.END_OF_INPUT)) {
      if (!token.getType().equals(TokenTypes.COMMA)) {
        node.addArgument(ExpressionParser.expressionNode(this));
      } else {
        consume(TokenTypes.COMMA);
      }
    }
    consume(TokenTypes.EOL, TokenTypes.END_OF_INPUT);

    return node;
  }

  private OpcodeNode opcode() {
    var opcode = new OpcodeNode(token);
    consume(TokenTypes.OPCODE);
    token = getCurrentToken();

    while (!token.getType().equals(TokenTypes.EOL)
        && !token.getType().equals(TokenTypes.END_OF_INPUT)) {
      if (token.getType().equals(TokenTypes.COMMA)) {
        consume(TokenTypes.COMMA);
        continue;
      }
      opcode.addChild(new OpcodeArgumentNode(getCurrentToken()));
      consume(getCurrentToken().getType());
    }

    consumeAndClear(TokenTypes.EOL, TokenTypes.END_OF_INPUT);
    return opcode;
  }

  /** Confirms that the current token is expected and advances to the next token. */
  public void consume(TokenTypes... types) {
    final var typesList = Arrays.asList(types);
    if (typesList.contains(token.getType())) {
      advanceToken();
    } else {
      throw new ParseException(
          "Unexpected Token.  One of " + typesList + " was expected.", getCurrentToken());
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
    var directiveToken = token;
    consume(TokenTypes.DIRECTIVE);

    var node = DirectiveUtils.createDirectiveNode(directiveName, directiveToken);
    var nodeParser = DirectiveUtils.getParser(node.getDirectiveType());
    node.setArguments(nodeParser.arguments(this));

    if (node.hasBody()) {
      node.setBody(nodeParser.body(this, getCurrentToken()));
    }

    if (node instanceof MacroNode) {
      var macroNode = (MacroNode) node;

      macroMap.put(macroNode.getName(), Optional.of(macroNode));
    }
    return node;
  }

  /** Move the token past any whitespace / comments */
  public void clearWhiteSpaceTokens() {
    var testToken = getCurrentToken();

    while (testToken != null && TokenTypes.EOL.equals(testToken.getType())) {
      consume(TokenTypes.EOL);
      testToken = getCurrentToken();
    }
  }

  public Token peekNextToken() {
    return scanner.peekNextToken();
  }

  public Map<String, Optional<MacroNode>> getMacroMap() {
    return Collections.unmodifiableMap(macroMap);
  }

  public Set<String> getIncludes() {
    return Collections.unmodifiableSet(includes);
  }

  public List<ErrorNode> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  public void addVisitor(Visitor visitor) {
    this.visitors.add(visitor);
  }
}
