package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {

  private final NodeTypes type;
  private final List<Node> children = new ArrayList<>();

  public Node(NodeTypes nodeType) {
    this.type = nodeType;
  }

  public final NodeTypes getType() {
    return type;
  }

  public List<Node> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public final Node addChild(Node childNode) {
    children.add(childNode);
    return this;
  }
}
