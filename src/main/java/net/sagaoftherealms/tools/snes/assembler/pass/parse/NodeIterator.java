package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeIterator implements Iterator<Node> {

  private Node node;
  private boolean returned = false;

  private int childIndex = 0; //Index of child we are iterating
  private Iterator<Node> childIterator;

  public NodeIterator(Node node) {
    this.node = node;
  }

  @Override
  public boolean hasNext() {
    return !returned;
  }

  @Override
  public Node next() {
    if (returned) {
      throw new NoSuchElementException("No more elements");
    }

    var children = node.getChildren();

    if (children == null || children.isEmpty()) {
      return returnNode();
    } else {
      return returnChild();
    }
  }

  private Node returnChild() {
    if (childIterator == null || !childIterator.hasNext()) {
      return returnNextChild();
    } else {
      return childIterator.next();
    }
  }

  private Node returnNextChild() {
    if (childIndex >= node.getChildren().size()) { //All children consumed, return this
      return returnNode();
    } else {//create next child iterator and return first item.
      childIterator = node.getChildren().get(childIndex++).iterator();
      return childIterator.next();
    }
  }

  private Node returnNode() {
    returned = true;
    return node;
  }
}
