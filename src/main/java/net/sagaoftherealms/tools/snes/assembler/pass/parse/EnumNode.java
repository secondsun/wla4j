package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class EnumNode extends Node {

    private final Token startAddress;

    public EnumNode(Token startAddress) {
        this.startAddress = startAddress;
    }

    public Token getAddress() {
        return startAddress;
    }

}
