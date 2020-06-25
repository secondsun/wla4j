package dev.secondsun.wla4j.assembler.pass.parse.visitor;

import dev.secondsun.wla4j.assembler.pass.parse.Node;

@FunctionalInterface
public interface Visitor {
  void visit(Node node);
}
