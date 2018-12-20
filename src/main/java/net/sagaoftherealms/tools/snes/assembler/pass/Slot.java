package net.sagaoftherealms.tools.snes.assembler.pass;

public class Slot {

  private int address;
  private int size;
  private Slot next;

  public Slot() {}

  public Slot(int address, int size) {
    this.address = address;
    this.size = size;
  }

  public Slot getNext() {
    return next;
  }

  public void setNext(Slot next) {
    this.next = next;
  }

  public int getAddress() {
    return address;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
}
