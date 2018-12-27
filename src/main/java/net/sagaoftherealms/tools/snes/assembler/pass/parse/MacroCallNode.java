package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.List;

public class MacroCallNode extends Node {

  private final String macroNode;

  public MacroCallNode(String macro) {
    super(NodeTypes.MACRO_CALL);
    this.macroNode = macro;
  }

  public String getMacroNode() {
    return macroNode;
  }

  public void addArgument(Node argument) {
    addChild(argument);
  }

  public List<Node> getArguments() {
    return getChildren();
  }
}
