package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.control.IfBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.NumericExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveUtils;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.control.IfParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ConstantNode;

/**
 * This class parses the body of directives which have definitions.
 *
 * <p>IE RAMSECTION, ENUM, UNION, and STRUCT
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
      case UNION:
        endDirective = AllDirectives.ENDU;
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
      } else if (token.getType().equals(TokenTypes.DIRECTIVE)) {
        var ifDirective = AllDirectives.valueOf(token.getString().replace(".", "").toUpperCase());
        if (IfParser.getIfDirectives().contains(ifDirective)) {
          body.addChild(makeIfNode(parser, token));
        } else if (AllDirectives.UNION.getPattern().startsWith(token.getString().toUpperCase())) {
          body.addChild(makeUnionNode(parser, token));
        } else {
          throw new ParseException("Unexpected token ", token);
        }
      } else {
        throw new ParseException("Unexpected token ", token);
      }

      token = parser.getCurrentToken();

      if (token.getType().equals(TokenTypes.END_OF_INPUT)) {
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

  private Node makeUnionNode(SourceParser parser, Token token) {
    parser.consume(TokenTypes.DIRECTIVE);
    var directive = AllDirectives.valueOf(token.getString().replace(".", "").toUpperCase());
    var unionNode = (UnionNode) DirectiveUtils.createDirectiveNode(directive.getName(), token);

    var parentNode = unionNode;
    token = parser.getCurrentToken();

    // Is this a named union?
    switch (token.getType()) {
      case EOL:
        parser.consumeAndClear(TokenTypes.EOL);
        unionNode.setArguments(new DirectiveArgumentsNode(token));
        break;
        // noName
      case LABEL:
        parser.consumeAndClear(TokenTypes.LABEL);
        unionNode.setArguments(new DirectiveArgumentsNode(token));
        break;
      default:
        throw new ParseException("Expecting LABEL or new line", token);
    }

    unionNode.setBody(new DirectiveBodyNode(unionNode.getSourceToken()));

    // makeBody
    // NEXTU or ENDU or definion, nothing else
    while (true) {
      token = parser.getCurrentToken();

      switch (token.getType()) {
        case DIRECTIVE:
          var bodyDirective =
              AllDirectives.valueOf(token.getString().replace(".", "").toUpperCase());
          switch (bodyDirective) {
            case UNION:
              unionNode.getBody().addChild(makeUnionNode(parser, token));
              break;
            case ENDU:
              parser.consumeAndClear(TokenTypes.DIRECTIVE);
              return parentNode;
            case NEXTU:
              unionNode.setNextUnion(
                  (UnionNode) DirectiveUtils.createDirectiveNode(directive.getName(), token));
              unionNode = unionNode.nextUnion.get();
              parser.consume(TokenTypes.DIRECTIVE);
              token = parser.getCurrentToken();
              if (token.getType() == TokenTypes.EOL) {
                parser.consumeAndClear(TokenTypes.EOL);
                unionNode.setArguments(new DirectiveArgumentsNode(token));
                unionNode.setBody(new DirectiveBodyNode(unionNode.getSourceToken()));
              } else if (token.getType() == TokenTypes.LABEL) {
                parser.consumeAndClear(TokenTypes.LABEL);
                unionNode.setArguments(new DirectiveArgumentsNode(token));
                unionNode.setBody(new DirectiveBodyNode(unionNode.getSourceToken()));
              }
              token = parser.getCurrentToken();
              break;
            default:
              throw new ParseException("Unexpected directive", token);
          }
          break;

        case LABEL:
          unionNode.getBody().addChild(makeDefinitionNode(parser, token));
          break;
        default:
          throw new ParseException("Expecting LABEL or ENDU or NEXTU", token);
      }
    }
  }

  private Node makeDefinitionNode(SourceParser parser, Token token) {
    parser.consumeAndClear(TokenTypes.LABEL);

    var bodyNode = new DefinitionNode(TokenUtil.getLabelName(token), token);

    token = parser.getCurrentToken();

    parser.consumeAndClear(TokenTypes.LABEL, TokenTypes.DIRECTIVE); // .DB and .DW are allowed

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
          var expression = ExpressionParser.expressionNode(parser);
          bodyNode.setSize((NumericExpressionNode) expression);
          parser.consumeAndClear(TokenTypes.EOL);
          break;
        }
      case "DSW":
        {
          var expression = ExpressionParser.expressionNode(parser);

          // We have to fake a double expression
          var constant = new ConstantNode(2, token);

          var doubleExpression = new NumericExpressionNode(token);
          doubleExpression.addChild(expression);

          doubleExpression.addChild(constant);
          doubleExpression.setOperationType(OperationType.MULTIPLY);
          bodyNode.setSize(doubleExpression);
          parser.consumeAndClear(TokenTypes.EOL);
          break;
        }
      case "INSTANCEOF": // TODO: Sizes of structs may be expressions, but I don't want to deal with
        // that yet
        token = parser.getCurrentToken();
        parser.consumeAndClear(TokenTypes.LABEL);

        bodyNode.setStructName(TokenUtil.getLabelName(token), token);
        bodyNode.setSize((new ConstantNode(1, token)));
        token = parser.getCurrentToken();

        if (TokenTypes.NUMBER.equals(token.getType())) {
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
      var firstToken = token;
      try {
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
      } catch (ParseException ex) {
        firstToken.getPosition().setEndLine(token.getPosition().getEndLine());
        firstToken.getPosition().setEndOffset(token.getPosition().getEndOffset());
        throw new ParseException(ex.getMessage(), firstToken);
      }

      parser.consumeAndClear(TokenTypes.DIRECTIVE); // Consume endif

      return new IfBodyNode(thenBody, elseBody, thenBody.getSourceToken());
    }
  }
}
