package dev.secondsun.wla4j.assembler.pass.parse.directive.control;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;

public class IfParser extends GenericDirectiveParser {

  private static final Set<AllDirectives> IF_DIRECTIVES =
      EnumSet.of(
          AllDirectives.IF,
          AllDirectives.IFNEQ,
          AllDirectives.IFLE,
          AllDirectives.IFDEF,
          AllDirectives.IFDEFM,
          AllDirectives.IFEQ,
          AllDirectives.IFEXISTS,
          AllDirectives.IFGR,
          AllDirectives.IFGREQ,
          AllDirectives.IFLEEQ,
          AllDirectives.IFNDEF,
          AllDirectives.IFNDEFM);
  private final AllDirectives directive;

  public static Set<AllDirectives> getIfDirectives() {
    return Collections.unmodifiableSet(IF_DIRECTIVES);
  }

  public IfParser(AllDirectives type) {
    super(type);
    if (!IF_DIRECTIVES.contains(type)) {
      throw new IllegalArgumentException(
          ("If directive required.  Directive provided was actually " + type));
    }

    this.directive = type;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode(parser.getCurrentToken());

    arguments.add(ExpressionParser.expressionNode(parser));

    if (directive.getPattern().trim().split(" ").length
        > 2) { // Magic knowledge about AllDirectives.  See their patterns
      arguments.add(ExpressionParser.expressionNode(parser));
    }

    parser.consumeAndClear(TokenTypes.EOL, TokenTypes.END_OF_INPUT);

    return arguments;
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token token) {
    DirectiveBodyNode thenBody = new DirectiveBodyNode(parser.getCurrentToken());
    DirectiveBodyNode elseBody = new DirectiveBodyNode(parser.getCurrentToken());
    var node = parser.nextNode();
    while (node.getType() != NodeTypes.DIRECTIVE
        || (((DirectiveNode) node).getDirectiveType() != AllDirectives.ELSE
            && ((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDIF)) {
      thenBody.addChild(node);
      node = parser.nextNode();
    }

    if (node.getType() == NodeTypes.DIRECTIVE
        && ((DirectiveNode) node).getDirectiveType() == AllDirectives.ELSE) {

      node = parser.nextNode();

      if (node == null) {
        throw new ParseException(".endif not found", token);
      }

      while (node.getType() != NodeTypes.DIRECTIVE
          || (((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDIF)) {
        elseBody.addChild(node);
        node = parser.nextNode();
        if (node == null) {
          throw new ParseException(".endif not found", token);
        }
      }
    } else if (node.getType() == NodeTypes.DIRECTIVE
        && ((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDIF) {
      throw new ParseException("Expected End or else", parser.getCurrentToken());
    }

    return new IfBodyNode(thenBody, elseBody, token);
  }
}
