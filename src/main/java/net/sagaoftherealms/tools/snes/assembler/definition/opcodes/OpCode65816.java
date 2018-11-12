package net.sagaoftherealms.tools.snes.assembler.definition.opcodes;

public class OpCode65816 extends OpCode {

  private final int skip_xbit;

  public OpCode65816(String op, int hex, int type, int skip_xbit) {
    super(op, hex, type);
    this.skip_xbit = skip_xbit;
  }

  public int getSkipXbit() {
    return skip_xbit;
  }
}
