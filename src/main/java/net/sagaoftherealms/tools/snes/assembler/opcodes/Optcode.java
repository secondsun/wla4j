package net.sagaoftherealms.tools.snes.assembler.opcodes;

public class Optcode {
    final String op;
    final int type, hex, branch;

    public Optcode(String op, int type, int hex, int branch) {
        this.op = op;
        this.type = type;
        this.hex = hex;
        this.branch = branch;
    }
}
