package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import java.util.List;

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
