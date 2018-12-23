package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionNode;

public class DirectiveArgumentsNode extends Node {

  protected final List<ExpressionNode> arguments = new ArrayList<>();

  public DirectiveArgumentsNode() {
    super(NodeTypes.DIRECTIVE_ARGUMENTS);
  }

  public String getString(int index) {
    return (String)(arguments.get(index).evaluate());
  }

  public DirectiveArgumentsNode add(String argumentValue) {
    arguments.add(new StringExpressionNode(argumentValue));
    return this;
  }
  public DirectiveArgumentsNode add(ExpressionNode argumentValue) {
    arguments.add(argumentValue);
    return this;
  }

  public int size() {
    return arguments.size();
  }
}
