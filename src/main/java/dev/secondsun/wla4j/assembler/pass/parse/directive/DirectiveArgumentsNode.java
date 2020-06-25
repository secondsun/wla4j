package dev.secondsun.wla4j.assembler.pass.parse.directive;

import java.util.stream.Collectors;

import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionNode;

public class DirectiveArgumentsNode extends Node {

  public DirectiveArgumentsNode(Token token) {
    super(NodeTypes.DIRECTIVE_ARGUMENTS, token);
  }

  public String getString(int index) {
    return (((ExpressionNode) getChildren().get(index)).evaluate()) + "";
  }

  public DirectiveArgumentsNode add(ExpressionNode argumentValue) {
    addChild(argumentValue);
    return this;
  }

  public int size() {
    return getChildren().size();
  }

  protected String safeGet(int index) {
    var argument = (ExpressionNode) getChildren().get(index);
    if (argument == null) {
      return null;
    } else {
      return String.valueOf(argument.evaluate());
    }
  }

  public int getInt(int index) {
    var argument = (ExpressionNode) getChildren().get(index);
    if (argument == null) {
      throw new RuntimeException("No argument");
    } else {
      return (int) argument.evaluate();
    }
  }

  @Override
  public String toString() {
    if (getChildren().isEmpty()) {
      return "";
    }
    return getChildren().stream()
        .map(obj -> obj == null ? "" : obj.toString())
        .collect(Collectors.joining(", "));
  }
}
