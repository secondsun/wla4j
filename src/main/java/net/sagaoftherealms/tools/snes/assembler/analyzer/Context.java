package net.sagaoftherealms.tools.snes.assembler.analyzer;


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
  private boolean snesDefined;
  private String snesHeaderId;
  private String snesHeaderName;
  private SNESRomMode snesRomMode;
  private Integer cartridgeType;
  private Integer romSize;
  private Integer sramSize;
  private Integer country;
  private Integer license;
  private Integer version;

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
    this.cartRomBankUsageTable = new byte[maxAddress];
  }

  public byte[] getCartRomBank() {
    return cartRomBank;
  }

  public byte[] getCartRomBankUsageTable() {
    return cartRomBankUsageTable;
  }

  public boolean getSnesDefined() {
    return snesDefined;
  }

  public void setSnesDefined(boolean snesDefined) {
    this.snesDefined = snesDefined;
  }

  public void setSnesHeaderId(String snesHeaderId) {
    this.snesHeaderId = snesHeaderId;
  }

  public String getSnesHeaderId() {
    return snesHeaderId;
  }

  public void setSnesHeaderName(String snesHeaderName) {
    this.snesHeaderName = snesHeaderName;
  }

  public String getSnesHeaderName() {
    return snesHeaderName;
  }

  public void setSnesRomMode(SNESRomMode snesRomMode) {
    this.snesRomMode = snesRomMode;
  }

  public SNESRomMode getSnesRomMode() {
    return snesRomMode;
  }

  public void setCartridgeType(Integer cartridgeType) {
    this.cartridgeType = cartridgeType;
  }

  public Integer getCartridgeType() {
    return cartridgeType;
  }

  public void setRomSize(Integer romSize) {
    this.romSize = romSize;
  }

  public Integer getRomSize() {
    return romSize;
  }

  public void setSramSize(Integer sramSize) {
    this.sramSize = sramSize;
  }

  public Integer getSramSize() {
    return sramSize;
  }

  public void setCountry(Integer country) {
    this.country = country;
  }

  public Integer getCountry() {
    return country;
  }

  public void setLicense(Integer license) {
    this.license = license;
  }

  public Integer getLicense() {
    return license;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Integer getVersion() {
    return version;
  }
}
