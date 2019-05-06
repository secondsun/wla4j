package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.Visitor;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

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

  public final void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
