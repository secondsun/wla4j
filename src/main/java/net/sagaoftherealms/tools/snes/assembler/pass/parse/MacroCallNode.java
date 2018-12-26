package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;

public class MacroCallNode extends Node {

  private final MacroNode macroNode;

  public MacroCallNode(MacroNode macro) {
    super(NodeTypes.MACRO_CALL);
    this.macroNode = macro;
  }

  public MacroNode getMacroNode() {
    return macroNode;
  }

  public void addArgument(Node argument) {
    addChild(argument);
  }

  public List<Node> getArguments() {
    return getChildren();
  }
}
