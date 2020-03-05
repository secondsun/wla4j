package net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;

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
