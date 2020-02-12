package net.sagaoftherealms.tools.snes.assembler.analyzer;

public class Context {
  private boolean memoryMapSet;
  private Integer slotSize;

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
}
