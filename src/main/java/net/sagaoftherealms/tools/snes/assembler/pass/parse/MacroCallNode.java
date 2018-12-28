package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class MacroCallNode extends Node {

  private final String macroNode;

  public MacroCallNode(String macro, Token token) {
    super(NodeTypes.MACRO_CALL, token);
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
