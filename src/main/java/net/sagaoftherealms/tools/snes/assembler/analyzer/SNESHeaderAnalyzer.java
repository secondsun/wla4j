package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.snesheader.SnesDefinitionNode;

public class SNESHeaderAnalyzer extends AbstractAnalyzer {

  protected SNESHeaderAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    enforceDirectiveType(node, AllDirectives.SNESHEADER);
    var errors = new ArrayList<ErrorNode>();
    if (context.getSnesDefined()) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException("SNESHeader must be defined only once", node.getSourceToken())));
      return errors;
    } else {
      context.setSnesDefined(true);
    }

    for (Node headerNode : node.getBody().getChildren()) {
      if (!headerNode.getType().equals(NodeTypes.SNES_HEADER_DEFINITION)) {
        throw new ParseException("Unexpected Node", headerNode.getSourceToken());
      }

      SnesDefinitionNode snesNode = (SnesDefinitionNode) headerNode;
      if (snesNode.getLabel().equalsIgnoreCase("ID")) {
        String id = snesNode.getName().get();
        if (id.length() < 1 || id.length() > 4) {
          errors.add(new ErrorNode(snesNode.getSourceToken(), new ParseException("ID must be between 1 and 4 characters", snesNode.getSourceToken())));
        }
        if (context.getSnesHeaderId() != null) {
          errors.add(new ErrorNode(snesNode.getSourceToken(), new ParseException("ID already Defined", snesNode.getSourceToken())));
        } else {
          context.setSnesHeaderId(snesNode.getName().get());
        }
      }
    }

    return errors;
  }

}
