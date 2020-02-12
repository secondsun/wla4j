package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;

public class MemoryMapAnalyzer {
  private final Context context;

  public MemoryMapAnalyzer(Context context) {
    this.context = context;
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

  /**
   * Checks a single memory map directive and updates the analyzer context
   *
   * @param node a memoryMap node to check for errors and add to the context
   * @return a list of errors, never null
   */
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {

    if (node.getDirectiveType().equals(AllDirectives.MEMORYMAP)) {

      List<ErrorNode> errors = new ArrayList<>();

      if (context.isMemoryMapSet()) {

        var errorNode =
            new ErrorNode(
                node.getSourceToken(),
                new ParseException("Memory Map is already defined", node.getSourceToken()));

        errors.add(errorNode);
      }

      DirectiveNode mmapNode = node;
      var bodyNode = mmapNode.getBody();
      var bodyChildren = bodyNode.getChildren();

      boolean foundDefaultSlot = false;
      var slotId = 0;
      for (Node child : bodyChildren) {
        if (child instanceof DefinitionNode) {
          var childNode = (DefinitionNode) child;
          if (childNode.getLabel().equalsIgnoreCase("defaultslot") && !foundDefaultSlot) {
            foundDefaultSlot = true;
            if (!(0 <= childNode.getSize().evaluate() && childNode.getSize().evaluate() <= 255)) {
              var errorNode =
                  new ErrorNode(
                      childNode.getSourceToken(),
                      new ParseException(
                          "Default slot address must be between 0 and 255",
                          childNode.getSourceToken()));

              errors.add(errorNode);
            }
          } else if (childNode.getLabel().equalsIgnoreCase("defaultslot") && foundDefaultSlot) {
            var errorNode =
                new ErrorNode(
                    childNode.getSourceToken(),
                    new ParseException(
                        "Default slot is already defined in this memory map",
                        childNode.getSourceToken()));

            errors.add(errorNode);
          } else if (childNode.getLabel().equalsIgnoreCase("slotsize")) {
            try {
              var slotsizeNode = (DefinitionNode) childNode;
              context.setSlotSize(slotsizeNode.getSize().evaluate());
            } catch (Exception ex) {
              var errorNode =
                  new ErrorNode(
                      childNode.getSourceToken(),
                      new ParseException(
                          "Slotsize needs an immediate value", childNode.getSourceToken()));

              errors.add(errorNode);
            }
          }
        } else if (child instanceof SlotNode) {
          var slotNode = (SlotNode) child;
          int slotNumber = slotNode.getNumber();

          if (0 > slotNumber || slotNumber > 255) {
            var errorNode =
                new ErrorNode(
                    slotNode.getSourceToken(),
                    new ParseException(
                        "SLOT needs a positive value (0-255) as an ID", slotNode.getSourceToken()));
            errors.add(errorNode);
          }

          if (slotNumber != slotId) {
            var errorNode =
                new ErrorNode(
                    slotNode.getSourceToken(),
                    new ParseException(
                        "Slots must be defined in order expected " + slotId,
                        slotNode.getSourceToken()));
            errors.add(errorNode);
          }

          int slotAddress = slotNode.getStart();

          if (slotAddress < 0) {
            var errorNode =
                new ErrorNode(
                    slotNode.getSourceToken(),
                    new ParseException(
                        "The starting address has to be a non-negative value.",
                        slotNode.getSourceToken()));
            errors.add(errorNode);
          }

          if (slotNode.getSize() == 0) {
            if (context.getSlotSize() == null) {
              var errorNode =
                  new ErrorNode(
                      slotNode.getSourceToken(),
                      new ParseException(
                          "The slot must have a value or a defaultslotsize set.",
                          slotNode.getSourceToken()));
              errors.add(errorNode);
            }
          }

          slotId++;
        }
      }

      if (!foundDefaultSlot) {
        ErrorNode error =
            new ErrorNode(
                mmapNode.getSourceToken(),
                new ParseException("No defaultslot applied", mmapNode.getSourceToken()));
        errors.add(error);
      }

      context.setMemoryMapSet(true);

      return errors;
    } else {
      throw new IllegalArgumentException(node + " is not a memory map directive");
    }
  }
}
