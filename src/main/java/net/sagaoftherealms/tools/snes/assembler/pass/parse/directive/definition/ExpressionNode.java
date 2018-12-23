package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

/**
 *
 * This is a marker that says a node is actually a calculation to be evaluated during analysis.
 *
 * @param <TYPE> one of String, Integer, Boolean, or Float.
 */
public interface ExpressionNode<TYPE> {

  TYPE evaluate();
}
