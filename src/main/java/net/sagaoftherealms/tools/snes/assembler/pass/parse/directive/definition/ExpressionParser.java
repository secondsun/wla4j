package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.LabelNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

/** Parses expressions in the body definition. */
public class ExpressionParser {

  private ExpressionParser(){}

  private static final List<TokenTypes> factorTypes =
      Arrays.asList(TokenTypes.NUMBER, TokenTypes.LABEL);
  private static final List<TokenTypes> operatorTypes =
      Arrays.asList(TokenTypes.MULTIPLY, TokenTypes.DIVIDE, TokenTypes.PLUS, TokenTypes.MINUS);

  public static NumericExpressionNode expressionNode(SourceParser parser) {

    NumericExpressionNode returnNode = new NumericExpressionNode();

    var token = parser.getCurrentToken();
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
        case DIVIDE:
        case PLUS:
        case MINUS:
          returnNode.setOperationType(token.getType());
          parser.consumeAndClear(token.getType());
          token = parser.getCurrentToken();
          if (!factorTypes.contains(token.getType())) {
            parsing = false;
          }
          break;
        default:
          throw new ParseException("Unexpected Token in expression", token);
      }
    }

    return returnNode;
  }

  private static void addNumberFactor(SourceParser parser, NumericExpressionNode returnNode, Token token) {
    ConstantNode numberNode = new ConstantNode(NodeTypes.NUMERIC_CONSTANT);
    numberNode.setValue(token.getString());
    parser.consumeAndClear(TokenTypes.NUMBER);
    returnNode.addChild(numberNode);
  }

  private static void addLabelFactor(SourceParser parser, NumericExpressionNode returnNode, Token token) {
    LabelNode labelNode = new LabelNode(token);
    parser.consumeAndClear(TokenTypes.LABEL);
    returnNode.addChild(labelNode);
  }
}
