package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser.expressionNode;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.DIRECTIVE;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveUtils;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ConstantNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.NumericExpressionNode;
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
  public DirectiveBodyNode body(SourceParser parser, Token initialToken) {

    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();
    var body = new DirectiveBodyNode(initialToken);
    while (token != null
        && !endDirective.getPattern().startsWith(token.getString().toUpperCase())) { // End on ENDE
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
    if (!IfParser.getIfDirectives().contains(directive)) {
      throw new ParseException("Directive was not an IF style directive", token);
    }
    var ifParser = new IfInDefinitionBodyParser(directive);

    ifNode.setArguments(ifParser.arguments(parser));
    ifNode.setBody(ifParser.body(parser, parser.getCurrentToken()));

    return ifNode;
  }

  private Node makeDefinitionNode(SourceParser parser, Token token) {
    parser.consumeAndClear(TokenTypes.LABEL);

    var bodyNode = new DefinitionNode(TokenUtil.getLabelName(token), token);

    token = parser.getCurrentToken();

    parser.consumeAndClear(TokenTypes.LABEL, DIRECTIVE); // .DB and .DW are allowed

    switch (token.getString().toUpperCase()) {
      case ".DW":
      case ".DB":
        bodyNode.setSize(new ConstantNode(0, token));
        break;
      case "DB":
      case "BYTE":
      case "BYT":
        bodyNode.setSize(new ConstantNode(1, token));
        break;
      case "DW":
      case "WORD":
        bodyNode.setSize(new ConstantNode(2, token));
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
          var constant = new ConstantNode(2, token);

          var doubleExpression = new NumericExpressionNode(token);
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
        bodyNode.setSize((new ConstantNode(1, token)));
        token = parser.getCurrentToken();

        if (NUMBER.equals(token.getType())) {
          bodyNode.setSize(TokenUtil.getInt(token), token);
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
    public DirectiveBodyNode body(SourceParser parser, Token token) {

      DirectiveBodyNode thenBody = new DirectiveBodyNode(token);
      DirectiveBodyNode elseBody = new DirectiveBodyNode(token);

      var currentBody = thenBody;

      token = parser.getCurrentToken();

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
            elseBody = new DirectiveBodyNode(token);
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

      return new IfBodyNode(thenBody, elseBody, thenBody.getSourceToken());
    }
  }
}
