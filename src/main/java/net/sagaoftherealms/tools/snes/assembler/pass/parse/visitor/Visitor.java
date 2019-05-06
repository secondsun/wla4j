package net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;

@FunctionalInterface
public interface Visitor {
  void visit(Node node);
}
