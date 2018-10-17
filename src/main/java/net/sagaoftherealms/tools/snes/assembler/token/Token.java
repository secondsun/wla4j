package net.sagaoftherealms.tools.snes.assembler.token;

public class Token {

    private final TokenTypes type;
    private final String string;

    public Token(String tokenString) {

        if (tokenString.startsWith("\"")) {
            type = TokenTypes.STRING;
            //trim quotes;
            tokenString = tokenString.substring(1, tokenString.length() - 1);
        } else if (tokenString.startsWith(".")) {
            type = TokenTypes.DIRECTIVE;
        } else if (tokenString.matches(TokenUtil.DECIMAL_NUMBER_REGEX) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_0) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_$) || tokenString.matches(TokenUtil.CHARACTER_NUMBER_REGEX) || tokenString.matches(TokenUtil.BINARY_NUMBER_REGEX)) {
            type = TokenTypes.NUMBER;
        } else if (Character.isAlphabetic(tokenString.charAt(0)) || tokenString.charAt(0) =='_' || tokenString.charAt(0) == '@' || tokenString.charAt(0) == '-' || tokenString.charAt(0) == '+'){
            type = TokenTypes.LABEL;
        } else {
            throw new IllegalArgumentException("Could not get TokenType for " + tokenString);
        }

        this.string = tokenString;

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
}
