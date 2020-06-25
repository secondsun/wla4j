package dev.secondsun.wla4j.assembler.pass.parse;

import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class SlotNode extends Node {

  private int number, start, size;

  public SlotNode(Token token) {
    super(NodeTypes.SLOT, token);
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
