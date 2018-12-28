package net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.AND;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.DIVIDE;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EQUAL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.GT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LEFT_PAREN;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MINUS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.MULTIPLY;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NOT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.OR;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.PLUS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.RIGHT_PAREN;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.OperationType;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/** Parses expressions in the body definition. */
public class ExpressionParser {

  private static List<TokenTypes> equalityTokens = Arrays.asList(NOT, TokenTypes.EQUAL);
  private static List<TokenTypes> comparisonTokens = Arrays.asList(GT, TokenTypes.LT);
  private static List<TokenTypes> shiftTokens = Arrays.asList(GT, TokenTypes.LT);

  private static final List<TokenTypes> termTokens = Arrays.asList(TokenTypes.PLUS, MINUS);
  private static final List<TokenTypes> factorTokens = Arrays.asList(MULTIPLY, DIVIDE);

  private ExpressionParser() {}

  public static ExpressionNode expressionNode(SourceParser parser) {

    var token = parser.getCurrentToken();

    if (token.getType().equals(TokenTypes.STRING)) {
      parser.consume(TokenTypes.STRING);
      return new StringExpressionNode(token.getString());
    }

    return bitwiseOrNode(parser);
  }

  private static NumericExpressionNode bitwiseOrNode(SourceParser parser) {
    NumericExpressionNode leftNode = bitWiseAndNode(parser);
    var token = parser.getCurrentToken();
    if (TokenTypes.OR.equals(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      toReturn.setOperationType(OperationType.OR);
      parser.consume(OR);
      toReturn.addChild(bitWiseAndNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode bitWiseAndNode(SourceParser parser) {
    NumericExpressionNode leftNode = equalityNode(parser);
    var token = parser.getCurrentToken();
    if (TokenTypes.AND.equals(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      toReturn.setOperationType(OperationType.AND);
      parser.consume(AND);
      toReturn.addChild(equalityNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode equalityNode(SourceParser parser) {
    NumericExpressionNode leftNode = comparisonNode(parser);
    var token = parser.getCurrentToken();
    if (equalityTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case EQUAL:
          parser.consume(EQUAL);
          parser.consume(EQUAL);
          toReturn.setOperationType(OperationType.EQUALS);
          break;
        case NOT:
          parser.consume(NOT);
          parser.consume(EQUAL);
          toReturn.setOperationType(OperationType.NOT_EQUAL);
          break;
        default:
          throw new ParseException("Unexpected comparison.", token);
      }
      toReturn.addChild(comparisonNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode comparisonNode(SourceParser parser) {
    NumericExpressionNode leftNode = shiftNode(parser);
    var token = parser.getCurrentToken();
    if (comparisonTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case GT:
          if (parser.peekNextToken().getType().equals(EQUAL)) {
            parser.consume(GT);
            parser.consume(EQUAL);
            toReturn.setOperationType(OperationType.GREATER_THAN_OR_EQUAL);
          } else {
            parser.consume(GT);
            toReturn.setOperationType(OperationType.GREATER_THAN);
          }
          break;
        case LT:
          if (parser.peekNextToken().getType().equals(EQUAL)) {
            parser.consume(LT);
            parser.consume(EQUAL);
            toReturn.setOperationType(OperationType.LESS_THAN_OR_EQUAL);
          } else {
            parser.consume(LT);
            toReturn.setOperationType(OperationType.LESS_THAN);
          }
          break;
        default:
          throw new ParseException("Unexpected comparison.", token);
      }
      toReturn.addChild(shiftNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode shiftNode(SourceParser parser) {
    NumericExpressionNode leftNode = termNode(parser);
    var token = parser.getCurrentToken();
    if (shiftTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case GT:
          if (parser.peekNextToken().getType().equals(GT)) {
            parser.consume(GT);
            parser.consume(GT);
            toReturn.setOperationType(OperationType.RIGHT_SHIFT);
          } else {
            return leftNode;
          }
          break;
        case LT:
          if (parser.peekNextToken().getType().equals(LT)) {
            parser.consume(LT);
            parser.consume(LT);
            toReturn.setOperationType(OperationType.LEFT_SHIFT);
          } else {
            return leftNode;
          }
          break;
        default:
          throw new ParseException("Unexpected shift.", token);
      }
      toReturn.addChild(termNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode termNode(SourceParser parser) {
    NumericExpressionNode leftNode = factorNode(parser);
    var token = parser.getCurrentToken();
    if (termTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case PLUS:
          toReturn.setOperationType(OperationType.ADD);
          parser.consume(PLUS);
          break;
        case MINUS:
          parser.consume(MINUS);
          toReturn.setOperationType(OperationType.SUBTRACT);
          break;
        default:
          throw new ParseException("Unexpected term.", token);
      }
      toReturn.addChild(factorNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode factorNode(SourceParser parser) {
    var token = parser.getCurrentToken();
    NumericExpressionNode leftNode = null;
    switch (token.getType()) {
      case LEFT_PAREN:
        parser.consume(LEFT_PAREN);
        leftNode = bitwiseOrNode(parser);
        parser.consume(RIGHT_PAREN);
        break;
      case MINUS:
        parser.consume(MINUS);
        token = parser.getCurrentToken();
        if (token.getType().equals(LABEL)) {
          parser.consume(LABEL);
          leftNode = new NegateIdentifierNode(token);
        } else { // assume number
          parser.consume(NUMBER);
          leftNode = new ConstantNode(-1 * TokenUtil.getInt(token));
        }
        break;
      case LT:
        parser.consume(LT);
        leftNode = new LowByteNode(bitwiseOrNode(parser));
        break;
      case GT:
        parser.consume(GT);
        leftNode = new HighByteNode(bitwiseOrNode(parser));
        break;
      case NUMBER:
        parser.consume(NUMBER);
        leftNode = new ConstantNode(TokenUtil.getInt(token));
        break;
      case LABEL:
        parser.consume(LABEL);
        leftNode = new IdentifierNode(token);
        break;
      default:
        break;
    }

    token = parser.getCurrentToken();

    if (factorTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode();
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case MULTIPLY:
          toReturn.setOperationType(OperationType.MULTIPLY);
          parser.consume(MULTIPLY);
          break;
        case DIVIDE:
          toReturn.setOperationType(OperationType.DIVIDE);
          parser.consume(DIVIDE);
          break;
        default:
          throw new ParseException("Unexpected factor.", token);
      }
      toReturn.addChild(factorNode(parser));
      return toReturn;
    }

    if (leftNode == null) {
      throw new ParseException("Unexpected factor.", token);
    }

    return leftNode;
  }
}
