package net.sagaoftherealms.tools.snes.assembler.definition.opcodes;

public class OpCode65816 extends OpCode {

  private final int skipXbit;

  public OpCode65816(String op, int hex, int type, int skipXbit) {
    super(op, hex, type);
    this.skipXbit = skipXbit;
  }

  public int getSkipXbit() {
    return skipXbit;
  }
}
