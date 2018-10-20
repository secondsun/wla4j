package net.sagaoftherealms.tools.snes.assembler.token;

public class Token {

    private final TokenTypes type;
    private final String string;

    public Token(String tokenString, TokenTypes type) {
        this.string = tokenString;
        this.type = type;
    }

    public TokenTypes getType() {
        return type;
    }

    public String getString() {
        return string;
    }

    public int getArgumentsCount() {
        return 0;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", string='" + string + '\'' +
                '}';
    }
}
