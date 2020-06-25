package dev.secondsun.wla4j.assembler.pass.parse.expression;

import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

/**
 * This is a marker that says a node is actually a calculation to be evaluated during analysis.
 *
 * @param <TYPE> one of String, Integer, Boolean, or Float.
 */
public abstract class ExpressionNode<TYPE> extends Node {

  public ExpressionNode(NodeTypes nodeType, Token token) {
    super(nodeType, token);
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

  @Override
  public String toString() {
    return evaluate().toString();
  }
}
