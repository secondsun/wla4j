package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class Node {

  private final NodeTypes type;
  private final List<Node> children = new ArrayList<>();
  private final Token sourceToken;

  public Node(NodeTypes nodeType, Token token) {
    this.type = nodeType;
    this.sourceToken = token;
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

  public Token getSourceToken() {
    return sourceToken;
  }
}
