package net.sagaoftherealms.tools.snes.assembler.opcodes;

public class GBOptCode extends OptCode{

    private final int branch;

    public GBOptCode(String op, int hex, int type, int branch) {
        super(op, hex, type);

        this.branch = branch;
    }

    public int getBranch() {
        return branch;
    }
}
