package net.sagaoftherealms.tools.snes.assembler.token;

public class Token {
    
    private final TokenTypes type;
    private final String string;

    public Token(String tokenString) {

        

        if (tokenString.startsWith("\"")) {
            type = TokenTypes.STRING;
            //trim quotes;
            tokenString = tokenString.substring(1, tokenString.length()-1);
        } else if (tokenString.startsWith(".")) {
            type = TokenTypes.DIRECTIVE;
        } else {
            throw new IllegalArgumentException("Could not get toketype for " + tokenString);
        }

        this.string = tokenString;

    }

    public TokenTypes getType() {
        return type;
    }

    public String getString() {
        return string;
    }
}
