package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.nio.ByteBuffer;

public class Context {
  private boolean memoryMapSet;
  private Integer slotSize;
  private int bankSize;
  private boolean bankSizeSet;
  private boolean romBanksDefined;
  private int romBanks;
  private int maxAddress;
  private byte[] cartRomBank;
  private byte[] cartRomBankUsageTable;

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

    public boolean getRomBanksDefined() {
        return romBanksDefined;
    }

    public void setRomBanksDefined(boolean romBanksDefined) {
        this.romBanksDefined = romBanksDefined;
    }

  public int getRomBanks() {
    return romBanks;
  }

  public void setRomBanks(int romBanks) {
    this.romBanks = romBanks;
  }


  public int getMaxAddress() {
    return maxAddress;
  }

  public void createRomBanks() {
    maxAddress = getBankSize() * getRomBanks();
    this.cartRomBank = new byte[maxAddress];
    this.cartRomBankUsageTable  = new byte[maxAddress];
  }

  public byte[] getCartRomBank() {
    return cartRomBank;
  }

  public byte[] getCartRomBankUsageTable() {
    return cartRomBankUsageTable;
  }
}
