package net.sagaoftherealms.tools.snes.assembler.opcodes;

public abstract class OptCode {
    private final String op;
    private final int  hex;
    private final int type;

    public OptCode(String op, int hex, int type) {
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
