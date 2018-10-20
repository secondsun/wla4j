package net.sagaoftherealms.tools.snes.assembler.opcodes;

public class GBOpCode extends OpCode {

    private final int branch;

    public GBOpCode(String op, int hex, int type, int branch) {
        super(op, hex, type);

        this.branch = branch;
    }

    public int getBranch() {
        return branch;
    }
}
