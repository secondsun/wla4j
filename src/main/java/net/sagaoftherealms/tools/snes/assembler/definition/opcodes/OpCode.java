package net.sagaoftherealms.tools.snes.assembler.definition.opcodes;

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
}
