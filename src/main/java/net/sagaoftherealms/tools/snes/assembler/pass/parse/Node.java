package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.Visitor;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

import java.util.*;

public abstract class Node implements Iterable<Node> {

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

  public final Node setChild(int index, Node childNode) {
    children.set(index, childNode);
    return this;
  }

  public Node getChildAt(int ordinal) {
    return children.get(ordinal);
  }

  public Token getSourceToken() {
    return sourceToken;
  }

  @Override
  public Iterator<Node> iterator() {
    return new NodeIterator(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Node)) return false;
    Node node = (Node) o;
    return type == node.type
        && Objects.equals(children, node.children)
        && Objects.equals(sourceToken, node.sourceToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, children, sourceToken);
  }

  public final void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
