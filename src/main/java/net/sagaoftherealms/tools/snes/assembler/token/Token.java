package net.sagaoftherealms.tools.snes.assembler.token;

public class Token {
    public static final Token EndOfFile = new Token();

    public TokenTypes getType() {
        return TokenTypes.STRING;
    }

    public String getString() {
        return "This is a String Token";
    }
}
