package net.sagaoftherealms.tools.snes.assembler.analyzer;

public class Context {
  private boolean memoryMapSet;
  private Integer slotSize;
  private int bankSize;
  private boolean bankSizeSet;

  public boolean isMemoryMapSet() {
    return memoryMapSet;
  }

  public void setMemoryMapSet(boolean memoryMapSet) {
    this.memoryMapSet = memoryMapSet;
  }

  public void setSlotSize(Integer slotSize) {
    this.slotSize = slotSize;
  }

  public Integer getSlotSize() {
    return slotSize;
  }

  public void setBankSize(int bankSize) {
    this.bankSize = bankSize;
  }

  public int getBankSize() {
    return bankSize;
  }

  public void setBankSizeSet(boolean bankSizeSet) {
    this.bankSizeSet = bankSizeSet;
  }

  public boolean getBankSizeSet() {
    return bankSizeSet;
  }
}
