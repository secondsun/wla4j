package dev.secondsun.wla4j.assembler.analyzer;

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
  private boolean snesNativeVector;
  private SNESRomSpeed snesRomSpeed;

  public boolean isMemoryMapSet() {
    return memoryMapSet;
  }

  public void setMemoryMapSet(boolean memoryMapSet) {
    this.memoryMapSet = memoryMapSet;
  }

  public Integer getSlotSize() {
    return slotSize;
  }

  public void setSlotSize(Integer slotSize) {
    this.slotSize = slotSize;
  }

  public int getBankSize() {
    return bankSize;
  }

  public void setBankSize(int bankSize) {
    this.bankSize = bankSize;
  }

  public boolean getBankSizeSet() {
    return bankSizeSet;
  }

  public void setBankSizeSet(boolean bankSizeSet) {
    this.bankSizeSet = bankSizeSet;
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

  public String getSnesHeaderId() {
    return snesHeaderId;
  }

  public void setSnesHeaderId(String snesHeaderId) {
    this.snesHeaderId = snesHeaderId;
  }

  public String getSnesHeaderName() {
    return snesHeaderName;
  }

  public void setSnesHeaderName(String snesHeaderName) {
    this.snesHeaderName = snesHeaderName;
  }

  public SNESRomMode getSnesRomMode() {
    return snesRomMode;
  }

  public void setSnesRomMode(SNESRomMode snesRomMode) {
    this.snesRomMode = snesRomMode;
  }

  public Integer getCartridgeType() {
    return cartridgeType;
  }

  public void setCartridgeType(Integer cartridgeType) {
    this.cartridgeType = cartridgeType;
  }

  public Integer getRomSize() {
    return romSize;
  }

  public void setRomSize(Integer romSize) {
    this.romSize = romSize;
  }

  public Integer getSramSize() {
    return sramSize;
  }

  public void setSramSize(Integer sramSize) {
    this.sramSize = sramSize;
  }

  public Integer getCountry() {
    return country;
  }

  public void setCountry(Integer country) {
    this.country = country;
  }

  public Integer getLicense() {
    return license;
  }

  public void setLicense(Integer license) {
    this.license = license;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public boolean getSnesNativeVector() {
    return snesNativeVector;
  }

  public void setSnesNativeVector(boolean snesNativeVector) {
    this.snesNativeVector = snesNativeVector;
  }

  public SNESRomSpeed getSnesRomSpeed() {
    return snesRomSpeed;
  }

  public void setSnesRomSpeed(SNESRomSpeed snesRomSpeed) {
    this.snesRomSpeed = snesRomSpeed;
  }
}
