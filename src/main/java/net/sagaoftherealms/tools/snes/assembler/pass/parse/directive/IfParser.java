package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import java.util.EnumSet;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;

public class IfParser extends GenericDirectiveParser {

  EnumSet<AllDirectives> IF_DIRECTIVES = EnumSet.of(AllDirectives.IF,
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

  public IfParser(AllDirectives type) {
    super(type);
    if (!IF_DIRECTIVES.contains(type)) {
      throw new IllegalArgumentException(("If directive required.  Directive provided was actually " + type));
    }
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    DirectiveBodyNode trueBody = new DirectiveBodyNode();
    DirectiveBodyNode elseBody = new DirectiveBodyNode();
    var node = parser.getCurrentNode();
    while (node.getType() != NodeTypes.DIRECTIVE || (((DirectiveNode)node).getDirectiveType() != AllDirectives.ELSE && ((DirectiveNode)node).getDirectiveType() != AllDirectives.ENDIF)) {
      trueBody.addChild(node);
      node = parser.nextNode();
    }

    if (node.getType() == NodeTypes.DIRECTIVE && ((DirectiveNode)node).getDirectiveType() != AllDirectives.ELSE) {
      while (node.getType() != NodeTypes.DIRECTIVE && ( ((DirectiveNode)node).getDirectiveType() != AllDirectives.ENDIF)) {
        trueBody.addChild(node);
        node = parser.nextNode();
      }
    } else if(node.getType() == NodeTypes.DIRECTIVE && ((DirectiveNode)node).getDirectiveType() != AllDirectives.ENDIF) {

    } else {
      throw new IllegalStateException("Expected End or else");
    }

    if (((DirectiveNode)node).getDirectiveType() != AllDirectives.ENDIF) {
      throw new IllegalStateException("Expected End");
    }

    return trueBody;
  }

}
