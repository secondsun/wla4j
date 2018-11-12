package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

public class DirectiveArgumentsNode extends Node {

  protected final List<String> arguments = new ArrayList<>();

  public DirectiveArgumentsNode() {
    super(NodeTypes.DIRECTIVE_ARGUMENTS);
  }

  public String get(int index) {
    return arguments.get(index);
  }

  public DirectiveArgumentsNode add(String argumentValue) {
    arguments.add(argumentValue);
    return this;
  }

  public int size() {
    return arguments.size();
  }
}
