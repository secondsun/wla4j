package dev.secondsun.wla4j.assembler.definition.opcodes;

public abstract class OpCode {

  private final String op;
  private final int hex;
  private final int type;

  public OpCode(String op, int hex, int type) {
    this.op = op;
    this.hex = hex;
    this.type = type;
  }

  public String getOp() {
    return op;
  }

  public int getHex() {
    return hex;
  }

  public int getType() {
    return type;
  }

  /**
   * This returns an array of opcodes from a stringed type in retro.json
   *
   * <p>gb == OpCodeGB, 65816 == OpCode65816 spc700 == OpCodeSpc700 z80 == opCodeZ80
   */
  public static OpCode[] from(String opcodesType) {
    opcodesType = opcodesType.toLowerCase();
    switch (opcodesType) {
      case "gb":
        return OpCodeGB.opcodes();
      case "65816":
        return OpCode65816.opcodes();
      case "spc700":
        return OpCodeSpc700.opcodes();
      case "z80":
        return OpCodeZ80.opcodes();
      default:
        throw new IllegalArgumentException(String.format("%s not recognized", opcodesType));
    }
  }
}
