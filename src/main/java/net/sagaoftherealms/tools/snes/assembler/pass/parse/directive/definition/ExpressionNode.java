package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

/**
 * This is a marker that says a node is actually a calculation to be evaluated during analysis.
 *
 * @param <TYPE> one of String, Integer, Boolean, or Float.
 */
public abstract class ExpressionNode<TYPE> extends Node {

  public ExpressionNode(NodeTypes nodeType) {
    super(nodeType);
  }

  public abstract TYPE evaluate();

  /**
   * As the compiler runs, we may not have all the information we need to evaluate an expression.
   *
   * <p>This performs that check.
   *
   * @return
   */
  public boolean canEvaluate() {
    return false;
  }
}
