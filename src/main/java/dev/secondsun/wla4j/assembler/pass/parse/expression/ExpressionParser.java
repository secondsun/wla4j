package dev.secondsun.wla4j.assembler.pass.parse.expression;

import java.util.Arrays;
import java.util.List;

import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.definition.OperationType;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;

/** Parses expressions in the body definition. */
public class ExpressionParser {

  private static List<TokenTypes> equalityTokens = Arrays.asList(TokenTypes.NOT, TokenTypes.EQUAL);
  private static List<TokenTypes> comparisonTokens = Arrays.asList(TokenTypes.GT, TokenTypes.LT);
  private static List<TokenTypes> shiftTokens = Arrays.asList(TokenTypes.GT, TokenTypes.LT);

  private static final List<TokenTypes> termTokens = Arrays.asList(TokenTypes.PLUS, TokenTypes.MINUS);
  private static final List<TokenTypes> factorTokens = Arrays.asList(TokenTypes.MULTIPLY, TokenTypes.DIVIDE);

  private ExpressionParser() {}

  public static ExpressionNode expressionNode(SourceParser parser) {

    var token = parser.getCurrentToken();

    if (token.getType().equals(TokenTypes.STRING)) {
      parser.consume(TokenTypes.STRING);
      return new StringExpressionNode(token.getString(), token);
    }

    return bitwiseOrNode(parser);
  }

  private static NumericExpressionNode bitwiseOrNode(SourceParser parser) {
    NumericExpressionNode leftNode = bitWiseAndNode(parser);
    var token = parser.getCurrentToken();
    if (TokenTypes.OR.equals(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      toReturn.setOperationType(OperationType.OR);
      parser.consume(TokenTypes.OR);
      toReturn.addChild(bitWiseAndNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode bitWiseAndNode(SourceParser parser) {
    NumericExpressionNode leftNode = equalityNode(parser);
    var token = parser.getCurrentToken();
    if (TokenTypes.AND.equals(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      toReturn.setOperationType(OperationType.AND);
      parser.consume(TokenTypes.AND);
      toReturn.addChild(bitWiseAndNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode equalityNode(SourceParser parser) {
    NumericExpressionNode leftNode = comparisonNode(parser);
    var token = parser.getCurrentToken();
    if (equalityTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case EQUAL:
          parser.consume(TokenTypes.EQUAL);
          parser.consume(TokenTypes.EQUAL);
          toReturn.setOperationType(OperationType.EQUALS);
          break;
        case NOT:
          parser.consume(TokenTypes.NOT);
          parser.consume(TokenTypes.EQUAL);
          toReturn.setOperationType(OperationType.NOT_EQUAL);
          break;
        default:
          throw new ParseException("Unexpected comparison.", token);
      }
      toReturn.addChild(equalityNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode comparisonNode(SourceParser parser) {
    NumericExpressionNode leftNode = shiftNode(parser);
    var token = parser.getCurrentToken();
    if (comparisonTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case GT:
          if (parser.peekNextToken().getType().equals(TokenTypes.EQUAL)) {
            parser.consume(TokenTypes.GT);
            parser.consume(TokenTypes.EQUAL);
            toReturn.setOperationType(OperationType.GREATER_THAN_OR_EQUAL);
          } else {
            parser.consume(TokenTypes.GT);
            toReturn.setOperationType(OperationType.GREATER_THAN);
          }
          break;
        case LT:
          if (parser.peekNextToken().getType().equals(TokenTypes.EQUAL)) {
            parser.consume(TokenTypes.LT);
            parser.consume(TokenTypes.EQUAL);
            toReturn.setOperationType(OperationType.LESS_THAN_OR_EQUAL);
          } else {
            parser.consume(TokenTypes.LT);
            toReturn.setOperationType(OperationType.LESS_THAN);
          }
          break;
        default:
          throw new ParseException("Unexpected comparison.", token);
      }
      toReturn.addChild(comparisonNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode shiftNode(SourceParser parser) {
    NumericExpressionNode leftNode = termNode(parser);
    var token = parser.getCurrentToken();
    if (shiftTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case GT:
          if (parser.peekNextToken().getType().equals(TokenTypes.GT)) {
            parser.consume(TokenTypes.GT);
            parser.consume(TokenTypes.GT);
            toReturn.setOperationType(OperationType.RIGHT_SHIFT);
          } else {
            return leftNode;
          }
          break;
        case LT:
          if (parser.peekNextToken().getType().equals(TokenTypes.LT)) {
            parser.consume(TokenTypes.LT);
            parser.consume(TokenTypes.LT);
            toReturn.setOperationType(OperationType.LEFT_SHIFT);
          } else {
            return leftNode;
          }
          break;
        default:
          throw new ParseException("Unexpected shift.", token);
      }
      toReturn.addChild(shiftNode(parser));
      return toReturn;
    }
    return leftNode;
  }

  private static NumericExpressionNode termNode(SourceParser parser) {
    NumericExpressionNode leftNode = factorNode(parser);
    var token = parser.getCurrentToken();

    if (TokenTypes.SIZE == token.getType()) {
      var sizeString = token.getString().toLowerCase();
      switch (sizeString) {
        case ".b":
          leftNode.setSize(Sizes.EIGHT_BIT);
          break;
        case ".w":
          leftNode.setSize(Sizes.SIXTEEN_BIT);
          break;
        case ".l":
          leftNode.setSize(Sizes.TWENTYFOUR_BIT);
          break;
      }
      parser.consume(TokenTypes.SIZE);
      token = parser.getCurrentToken();
    }

    if (termTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case PLUS:
          toReturn.setOperationType(OperationType.ADD);
          parser.consume(TokenTypes.PLUS);
          break;
        case MINUS:
          parser.consume(TokenTypes.MINUS);
          toReturn.setOperationType(OperationType.SUBTRACT);
          break;
        default:
          throw new ParseException("Unexpected term.", token);
      }
      toReturn.addChild(termNode(parser));
      return toReturn;
    }

    return leftNode;
  }

  private static NumericExpressionNode factorNode(SourceParser parser) {
    var token = parser.getCurrentToken();
    NumericExpressionNode leftNode = null;
    switch (token.getType()) {
      case LEFT_PAREN:
        parser.consume(TokenTypes.LEFT_PAREN);
        leftNode = bitwiseOrNode(parser);
        parser.consume(TokenTypes.RIGHT_PAREN);
        break;
      case MINUS:
        var minusToken = token;
        parser.consume(TokenTypes.MINUS);
        token = parser.getCurrentToken();
        if (token.getType().equals(TokenTypes.LABEL)) {
          parser.consume(TokenTypes.LABEL);
          leftNode = new NegateIdentifierNode(token);
        } else if (token.getType().equals(TokenTypes.NUMBER)) { // assume number
          parser.consume(TokenTypes.NUMBER);
          leftNode = new ConstantNode(-1 * TokenUtil.getInt(token), token);
        } else {
          leftNode = new IdentifierNode(minusToken);
        }
        break;
      case LT:
        parser.consume(TokenTypes.LT);
        leftNode = new LowByteNode(bitwiseOrNode(parser), token);
        break;
      case GT:
        parser.consume(TokenTypes.GT);
        leftNode = new HighByteNode(bitwiseOrNode(parser), token);
        break;
      case NUMBER:
        parser.consume(TokenTypes.NUMBER);
        leftNode = new ConstantNode(TokenUtil.getInt(token), token);
        break;
      case LABEL:
        parser.consume(TokenTypes.LABEL);
        leftNode = new IdentifierNode(token);
        break;
      case PLUS:
        parser.consume(TokenTypes.PLUS);
        leftNode = new IdentifierNode(token);
        break;
      default:
        break;
    }

    token = parser.getCurrentToken();

    if (factorTokens.contains(token.getType())) {
      NumericExpressionNode toReturn = new NumericExpressionNode(token);
      toReturn.addChild(leftNode);
      switch (token.getType()) {
        case MULTIPLY:
          toReturn.setOperationType(OperationType.MULTIPLY);
          parser.consume(TokenTypes.MULTIPLY);
          break;
        case DIVIDE:
          toReturn.setOperationType(OperationType.DIVIDE);
          parser.consume(TokenTypes.DIVIDE);
          break;
        default:
          throw new ParseException("Unexpected factor.", token);
      }
      toReturn.addChild(factorNode(parser));
      return toReturn;
    }

    if (TokenTypes.SIZE == token.getType()) {
      var sizeString = token.getString().toLowerCase();
      switch (sizeString) {
        case ".b":
        case ".w":
        case ".l":
      }
    }

    if (leftNode == null) {
      throw new ParseException("Unexpected factor.", token);
    }

    return leftNode;
  }
}
