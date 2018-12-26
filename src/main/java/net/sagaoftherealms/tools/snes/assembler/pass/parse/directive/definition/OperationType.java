package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.function.IntBinaryOperator;

public enum OperationType {
  MULTIPLY((left, right) -> left * right),
  ADD((left, right) -> left + right),
  DIVIDE((left, right) -> left / right),
  SUBTRACT((left, right) -> left - right),
  LEFT_SHIFT((left, right) -> left << right),
  RIGHT_SHIFT((left, right) -> left >> right),
  GREATER_THAN((left, right) -> left > right ? 1 : 0),
  LESS_THAN((left, right) -> left < right ? 1 : 0),
  LESS_THAN_OR_EQUAL((left, right) -> left <= right ? 1 : 0),
  AND((left, right) -> left & right),
  OR((left, right) -> left | right),
  EQUALS((left, right) -> left == right ? 1 : 0),
  NOT_EQUAL((left, right) -> left != right ? 1 : 0),
  GREATER_THAN_OR_EQUAL((left, right) -> left >= right ? 1 : 0);

  private final IntBinaryOperator evaluator;

  OperationType(IntBinaryOperator evaluator) {
    this.evaluator = evaluator;
  }

  public Integer evaluate(Integer left, Integer right) {
    return evaluator.applyAsInt(left, right);
  }
}
