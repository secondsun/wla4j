package net.sagaoftherealms.tools.snes.assembler.opcodes;

public class OptCode65816 extends  OptCode{

    private final int skip_xbit;

    public OptCode65816(String op, int hex, int type, int skip_xbit) {
        super(op, hex, type);
        this.skip_xbit = skip_xbit;
    }

    public int getSkipXbit() {
        return skip_xbit;
    }
}
