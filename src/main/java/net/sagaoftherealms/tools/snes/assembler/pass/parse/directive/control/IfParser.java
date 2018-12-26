package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.COMMA;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import java.util.EnumSet;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionParser;

public class IfParser extends GenericDirectiveParser {

  public static final EnumSet<AllDirectives> IF_DIRECTIVES =
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

  public IfParser(AllDirectives type) {
    super(type);
    if (!IF_DIRECTIVES.contains(type)) {
      throw new IllegalArgumentException(
          ("If directive required.  Directive provided was actually " + type));
    }

    this.directive = type;

  }

  public DirectiveArgumentsNode arguments(SourceParser parser) {
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode();

    arguments.add(ExpressionParser.expressionNode(parser));

    if (directive.getPattern().trim().split(" ").length > 2) {//Magic knowledge about AllDirectives.  See their patterns
      arguments.add(ExpressionParser.expressionNode(parser));
    }

    parser.consumeAndClear(EOL, END_OF_INPUT);

    return arguments;
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    DirectiveBodyNode thenBody = new DirectiveBodyNode();
    DirectiveBodyNode elseBody = new DirectiveBodyNode();
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

      while (node.getType() != NodeTypes.DIRECTIVE
          || (((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDIF)) {
        elseBody.addChild(node);
        node = parser.nextNode();
      }
    } else if (node.getType() == NodeTypes.DIRECTIVE
        && ((DirectiveNode) node).getDirectiveType() != AllDirectives.ENDIF) {
      throw new ParseException("Expected End or else", parser.getCurrentToken());
    }

    return new IfBodyNode(thenBody, elseBody);
  }
}
