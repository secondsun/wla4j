package net.sagaoftherealms.tools.snes.assembler.token;

import java.util.Arrays;
import java.util.List;

public class Token {

    private final TokenTypes type;
    private final String string;

    public Token(String tokenString) {
        final List<Character> operators = Arrays.asList(new Character[]{',','|','&','^','+','-','#','~','*','/','<','>','[',']','(',')'});

        if (tokenString.startsWith("\"")) {
            type = TokenTypes.STRING;
            //trim quotes;
            tokenString = tokenString.substring(1, tokenString.length() - 1);
        } else if (tokenString.startsWith(".")) {
            type = TokenTypes.DIRECTIVE;
        } else if (tokenString.matches(TokenUtil.DECIMAL_NUMBER_REGEX) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_0) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_$) || tokenString.matches(TokenUtil.CHARACTER_NUMBER_REGEX) || tokenString.matches(TokenUtil.BINARY_NUMBER_REGEX)) {
            type = TokenTypes.NUMBER;
        } else if (Character.isAlphabetic(tokenString.charAt(0)) || tokenString.charAt(0) =='_' || tokenString.charAt(0) == '@' ){
            type = TokenTypes.LABEL;
        } else if  (tokenString.length() == 1 && operators.contains(tokenString.charAt(0))) {
            type = operatorType(tokenString.charAt(0));
        } else {
            throw new IllegalArgumentException("Could not get TokenType for " + tokenString);
        }

        this.string = tokenString;

    }

    private TokenTypes operatorType(char operatorCharacter) {

        switch (operatorCharacter) {
            case ',':
                return TokenTypes.COMMA;
            case '|':
                return TokenTypes.OR;
            case '&':
                return TokenTypes.AND;
            case '^':
                return TokenTypes.POWER;
            case '+':
                return TokenTypes.PLUS;
            case '-':
                return TokenTypes.MINUS;
            case '#':
                return TokenTypes.MODULO;
            case '~':
                return TokenTypes.XOR;
            case '*':
                return TokenTypes.MULTIPLY;
            case '/':
                return TokenTypes.DIVIDE;
            case '<':
                return TokenTypes.LT;
            case '>':
                return TokenTypes.GT;
            case ']':
                return TokenTypes.RIGHT_BRACKET;
            case '[':
                return TokenTypes.LEFT_BRACKET;
            case ')':
                return TokenTypes.RIGHT_PAREN;
            case '(':
                return TokenTypes.LEFT_PAREN;
        }

        throw new IllegalArgumentException("Unknown Operator Type");
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
