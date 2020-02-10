package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.Directive;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;

public class MemoryMapAnalyzer {
  private final Context context;

  public MemoryMapAnalyzer(Context context) {
    this.context = context;
  }

  public List<ErrorNode> check(String fileName, List<Node> root) {

    List<DirectiveNode> mmapNodes =
        root.stream()
            .filter(node -> node.getType().equals(NodeTypes.DIRECTIVE))
            .map(node -> (DirectiveNode) node)
            .flatMap(MemoryMapAnalyzer::hasMemoryMapNode)
            .collect(Collectors.toList());

    if (mmapNodes.size() == 0) {
      return new ArrayList<>();
    }

    if (mmapNodes.size() > 1) {
      List<ErrorNode> toReturn;

      toReturn =
          mmapNodes
              .stream()
              .skip(1)
              .map(
                  node ->
                      new ErrorNode(
                          node.getSourceToken(),
                          new ParseException(
                              "Memory Map is already defined", node.getSourceToken())))
              .collect(Collectors.toList());

      return toReturn;
    }

    DirectiveNode mmapNode = mmapNodes.get(0);
    var bodyNode = mmapNode.getBody();
    var bodyChildren = bodyNode.getChildren();

    boolean foundDefaultSlot = false;

    for (Node child : bodyChildren) {
      if (child instanceof DefinitionNode) {
        var node = (DefinitionNode) child;
        if (node.getLabel().equalsIgnoreCase("defaultslot")) {
          foundDefaultSlot = true;
        }
      }
    }


    if(!foundDefaultSlot) {
      ErrorNode error = new ErrorNode(mmapNode.getSourceToken(), new ParseException("No defaultslot applied", mmapNode.getSourceToken()));
      return List.of(error);
    }

    return Collections.EMPTY_LIST;
  }

  private static Stream<DirectiveNode> hasMemoryMapNode(DirectiveNode node) {
    List<DirectiveNode> nodes = new ArrayList<>();
    if (node.getDirectiveType().equals(AllDirectives.MEMORYMAP)) {
      nodes.add(node);
    } else {
      for (Node child : node) {
        if (child instanceof DirectiveNode) {
          nodes.addAll(hasMemoryMapNode((DirectiveNode) child).collect(Collectors.toList()));
        }
      }
    }
    return nodes.stream();
  }

}
