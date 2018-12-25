package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.AND;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.DIVIDE;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MINUS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MULTIPLY;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.PLUS;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.LabelNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.NumericExpressionNode.OperationType;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

/** Parses expressions in the body definition. */
public class ExpressionParser {

  private ExpressionParser() {}

  private static final List<TokenTypes> factorTypes =
      Arrays.asList(TokenTypes.NUMBER, TokenTypes.LABEL);
  private static final List<TokenTypes> operatorTypes =
      Arrays.asList(MULTIPLY, TokenTypes.DIVIDE, TokenTypes.PLUS, MINUS, TokenTypes.GT, TokenTypes.LT, TokenTypes.AND, TokenTypes.OR, TokenTypes.EQUAL);

  public static ExpressionNode expressionNode(SourceParser parser) {

    var token = parser.getCurrentToken();

    if (token.getType().equals(TokenTypes.STRING)) {
      parser.consume(TokenTypes.STRING);
      return new StringExpressionNode(token.getString());
    }

    NumericExpressionNode returnNode = new NumericExpressionNode();

    boolean parsing = true;
    while (parsing) {
      switch (token.getType()) {
        case LABEL:
          addLabelFactor(parser, returnNode, token);
          token = parser.getCurrentToken();
          if (!operatorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case NUMBER:
          addNumberFactor(parser, returnNode, token);
          token = parser.getCurrentToken();
          if (!operatorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case MULTIPLY:
          returnNode.setOperationType(OperationType.MULTIPLY);
          parser.consume(MULTIPLY);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case DIVIDE:
          returnNode.setOperationType(OperationType.DIVIDE);
          parser.consume(DIVIDE);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case PLUS:
          returnNode.setOperationType(OperationType.ADD);
          parser.consume(PLUS);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case MINUS:
          returnNode.setOperationType(OperationType.SUBTRACT);
          parser.consume(MINUS);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case OR:
          returnNode.setOperationType(OperationType.OR);
          parser.consume(TokenTypes.OR);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case AND:
          returnNode.setOperationType(OperationType.AND);
          parser.consume(AND);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        case GT: //Right shift >>
          parser.consume(TokenTypes.GT);
          parsing = calculateGt(parser, returnNode);
          token = parser.getCurrentToken();
          break;
        case LT: //Left shift <<
          parser.consume(TokenTypes.LT);
          parsing = calculateLt(parser, returnNode);
          token = parser.getCurrentToken();
          break;
        case EQUAL: // == equality
          parser.consume(TokenTypes.EQUAL);
          parser.consume(TokenTypes.EQUAL);
          returnNode.setOperationType(OperationType.EQUALS);
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        default:
          throw new ParseException("Unexpected Token in expression", token);
      }
    }

    if (returnNode.getOperationType() != null && returnNode.getChildren().size() < 2) {
      throw new ParseException("Invalid expression", token);
    }

    return returnNode;
  }

  private static boolean calculateLt(SourceParser parser,
      NumericExpressionNode returnNode) {
    boolean parsing = true;

    var token = parser.getCurrentToken();
    switch (token.getType()) {
      case NUMBER:
      case LABEL:
        returnNode.setOperationType(OperationType.LESS_THAN);
        break;
      case EQUAL:
        returnNode.setOperationType(OperationType.LESS_THAN_OR_EQUAL);
        parser.consume(TokenTypes.EQUAL);
        break;
      case LT:
        returnNode.setOperationType(OperationType.LEFT_SHIFT);
        parser.consume(TokenTypes.LT);
        break;
      default: throw new ParseException("Was expecting one of =,>,Number,Label", token);
    }
    token = parser.getCurrentToken();
    if (!factorTypes.contains(token.getType())) {
      parsing = false;
    }

    return parsing;
  }

  private static boolean calculateGt(SourceParser parser,
      NumericExpressionNode returnNode) {
    boolean parsing = true;

    var token = parser.getCurrentToken();
    switch (token.getType()) {
      case NUMBER:
      case LABEL:
        returnNode.setOperationType(OperationType.GREATER_THAN);
        break;
      case EQUAL:
        returnNode.setOperationType(OperationType.GREATER_THAN_OR_EQUAL);
        parser.consume(TokenTypes.EQUAL);

        break;
      case GT:
        returnNode.setOperationType(OperationType.RIGHT_SHIFT);
        parser.consume(TokenTypes.GT);

        break;
      default: throw new ParseException("Was expecting one of =,>,Number,Label", token);
    }

    token = parser.getCurrentToken();

    if (!factorTypes.contains(token.getType())) {
      parsing = false;
    }

    return parsing;
  }

  private static void addNumberFactor(
      SourceParser parser, NumericExpressionNode returnNode, Token token) {
    ConstantNode numberNode = new ConstantNode(NodeTypes.NUMERIC_CONSTANT);
    numberNode.setValue(token.getString());
    parser.consume(TokenTypes.NUMBER);
    returnNode.addChild(numberNode);
  }

  private static void addLabelFactor(
      SourceParser parser, NumericExpressionNode returnNode, Token token) {
    LabelNode labelNode = new LabelNode(token);
    parser.consume(TokenTypes.LABEL);
    returnNode.addChild(labelNode);
  }
}
