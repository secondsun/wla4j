package dev.secondsun.wla4j.assembler.pass.parse.visitor;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.directive.macro.MacroNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** When a macro is defined, add the relevant information to a Map. */
public class MacroDefinitionVisitor implements Visitor {
  private final Map<String, Optional<MacroNode>> macroNames = new HashMap<>();

  @Override
  public void visit(Node node) {
    if (node instanceof MacroNode) {
      var macroNode = (MacroNode) node;
      macroNames.put(macroNode.getName(), Optional.of(macroNode));
    }
  }

  public Map<String, Optional<MacroNode>> getMacroNames() {
    return Collections.unmodifiableMap(macroNames);
  }
}
