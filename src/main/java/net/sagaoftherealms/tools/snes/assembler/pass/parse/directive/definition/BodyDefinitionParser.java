package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionParser.expressionNode;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.DIRECTIVE;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveUtils;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.NumericExpressionNode.OperationType;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/**
 * This class parses the body of directives which have definitions.
 *
 * <p>IE RAMSECTION, ENUM, and STRUCT
 */
public abstract class BodyDefinitionParser extends GenericDirectiveParser {

  private final AllDirectives endDirective;

  public BodyDefinitionParser(AllDirectives type) {
    super(type);
    switch (type) {
      case ENUM:
        endDirective = AllDirectives.ENDE;
        break;
      case STRUCT:
        endDirective = AllDirectives.ENDST;
        break;
      case RAMSECTION:
        endDirective = AllDirectives.ENDS;
        break;
      default:
        throw new IllegalArgumentException("Unsupported type" + type);
    }
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    var body = new DirectiveBodyNode();
    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();

    while (token != null
        && !endDirective.getPattern().startsWith(token.getString())) { // End on ENDE
      // Expect the token to be the first label
      if (token.getType().equals(TokenTypes.LABEL)) {
        body.addChild(makeDefinitionNode(parser, token));
      } else if (token.getType().equals(DIRECTIVE)) {
        body.addChild(makeIfNode(parser, token));
      } else {
        throw new ParseException("Unexpected token ", token);
      }

      token = parser.getCurrentToken();

      if (token.getType().equals(END_OF_INPUT)) {
        break;
      }
    }

    parser.consumeAndClear(TokenTypes.DIRECTIVE); // consume the .END? directives
    return body;
  }

  private Node makeIfNode(SourceParser parser, Token token) {
    parser.consumeAndClear(TokenTypes.DIRECTIVE);
    var directive = AllDirectives.valueOf(token.getString().replace(".", "").toUpperCase());
    var ifNode = DirectiveUtils.createDirectiveNode(directive.getName(), token);
    if (!IfParser.IF_DIRECTIVES.contains(directive)) {
      throw new ParseException("Directive was not an IF style directive", token);
    }
    var ifParser = new IfInDefinitionBodyParser(directive);

    ifNode.setArguments(ifParser.arguments(parser));
    ifNode.setBody(ifParser.body(parser));

    return ifNode;
  }

  private Node makeDefinitionNode(SourceParser parser, Token token) {
    parser.consumeAndClear(TokenTypes.LABEL);

    var bodyNode = new DefinitionNode(TokenUtil.getLabelName(token));

    token = parser.getCurrentToken();

    parser.consumeAndClear(TokenTypes.LABEL);

    switch (token.getString().toUpperCase()) {
      case "DB":
      case "BYTE":
      case "BYT":
        bodyNode.setSize(1);
        break;
      case "DW":
      case "WORD":
        bodyNode.setSize(2);
        break;
      case "DS":
      case "DSB":
        {
          var expression = expressionNode(parser);
          bodyNode.setSize((NumericExpressionNode) expression);
          parser.consumeAndClear(EOL);
          break;
        }
      case "DSW":
        {
          var expression = expressionNode(parser);

          // We have to fake a double expression
          var constant = new ConstantNode(2);

          var doubleExpression = new NumericExpressionNode();
          doubleExpression.addChild(expression);

          doubleExpression.addChild(constant);
          doubleExpression.setOperationType(OperationType.MULTIPLY);
          bodyNode.setSize(doubleExpression);
          parser.consumeAndClear(EOL);
          break;
        }
      case "INSTANCEOF": // TODO: Sizes of structs may be expressions, but I don't want to deal with
        // that yet
        token = parser.getCurrentToken();
        parser.consumeAndClear(TokenTypes.LABEL);

        bodyNode.setStructName(TokenUtil.getLabelName(token));
        bodyNode.setSize(1);
        token = parser.getCurrentToken();

        if (NUMBER.equals(token.getType())) {
          bodyNode.setSize(TokenUtil.getInt(token));
          parser.consumeAndClear(TokenTypes.NUMBER);
        }
        break;
      default:
        throw new ParseException("Unexpected type.", token);
    }

    return bodyNode;
  }

  private class IfInDefinitionBodyParser extends IfParser {

    public IfInDefinitionBodyParser(AllDirectives type) {
      super(type);
    }

    @Override
    public DirectiveBodyNode body(SourceParser parser) {
      DirectiveBodyNode thenBody = new DirectiveBodyNode();
      DirectiveBodyNode elseBody = new DirectiveBodyNode();

      var currentBody = thenBody;
      var token = parser.getCurrentToken();

      while (token != null
          && !token.getString().equalsIgnoreCase(".endif")
          && token.getType() != TokenTypes.END_OF_INPUT) {
        var tokenString = token.getString().toUpperCase().replace(".", "");
        switch (tokenString) {
          case "IF":
          case "IFNEQ":
          case "IFLE":
          case "IFDEF":
          case "IFDEFM":
          case "IFEQ":
          case "IFEXISTS":
          case "IFGR":
          case "IFGREQ":
          case "IFLEEQ":
          case "IFNDEF":
          case "IFNDEFM":
            currentBody.addChild(makeIfNode(parser, token));
            break;
          case "ELSE":
            currentBody = elseBody;
            parser.consumeAndClear(TokenTypes.DIRECTIVE);
            break;
          default:
            currentBody.addChild(makeDefinitionNode(parser, token));
            break;
        }

        token = parser.getCurrentToken();
      }
      parser.consumeAndClear(TokenTypes.DIRECTIVE); // Consume endif

      return new IfBodyNode(thenBody, elseBody);
    }
  }
}
