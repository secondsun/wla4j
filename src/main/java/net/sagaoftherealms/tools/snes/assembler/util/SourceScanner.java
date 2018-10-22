package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.token.Token;
import net.sagaoftherealms.tools.snes.assembler.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.token.TokenUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A stateful object that is used to read data from a {@link SourceFileDataMap}
 */
public class SourceScanner {
    private final SourceFileDataMap source;

    private int lineNumber = 0;
    private int linePosition = 0;
    private final List<String> opCodes;

    public SourceScanner(SourceFileDataMap source, OpCode[] opcodes) {
        this.source = source;
        this.opCodes = Arrays.asList(opcodes).stream()
                .map(OpCode::getOp)
                .map(code -> code.split(" ")[0])
                .collect(Collectors.toList());
    }

    public SourceDataLine getNextLine() {
        linePosition = 0;
        return source.getLine(++lineNumber);
    }

    public SourceDataLine getCurrentLine() {
        return source.getLine(lineNumber);
    }


    public Token getNextToken() {
        String tokenString = getNextTokenString();
        TokenTypes type;
        final List<Character> operators = Arrays.asList(new Character[]{',', '|', '&', '^', '+', '-', '#', '~', '*', '/', '<', '>', '[', ']', '(', ')'});
        final List<String> sizeTokens = Arrays.asList(new String[]{".b", ".w", ".l",".B", ".W", ".L"});

        if (tokenString.startsWith("\"")) {
            type = TokenTypes.STRING;
            //trim quotes;
            tokenString = tokenString.substring(1, tokenString.length() - 1);
        } else if (sizeTokens.contains(tokenString)) {
            type = TokenTypes.SIZE;
        } else if (tokenString.startsWith(".")) {
            type = TokenTypes.DIRECTIVE;
        } else if (tokenString.matches(TokenUtil.DECIMAL_NUMBER_REGEX) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_0) || tokenString.matches(TokenUtil.HEX_NUMBER_REGEX_$) || tokenString.matches(TokenUtil.CHARACTER_NUMBER_REGEX) || tokenString.matches(TokenUtil.BINARY_NUMBER_REGEX)) {
            type = TokenTypes.NUMBER;
        } else if (Character.isAlphabetic(tokenString.charAt(0)) || tokenString.charAt(0) == '_' || tokenString.charAt(0) == '@') {
            if (opCodes.contains(tokenString)) {
                type = TokenTypes.OPCODE;
            } else {
                type = TokenTypes.LABEL;
            }
        } else if (tokenString.length() == 1 && operators.contains(tokenString.charAt(0))) {
            type = operatorType(tokenString.charAt(0));
        } else {
            throw new IllegalArgumentException("Could not get TokenType for " + tokenString);
        }


        return new Token(tokenString, type);
    }


    private String getNextTokenString() {

        final List<Character> operators = Arrays.asList(new Character[]{',', '|', '&', '^', '+', '-', '#', '~', '*', '/', '<', '>', '[', ']', '(', ')'});

        if (lineNumber == 0) {
            getNextLine();
        }

        //get line where we left off reading
        var line = getCurrentLine();
        var sourceString = line.getDataLine();

        while (linePosition >= sourceString.length()) {
            getNextLine();
            line = getCurrentLine();
            sourceString = line.getDataLine();
        }

        char character = sourceString.charAt(linePosition);
        linePosition++;

        //Consume leading whitespace
        while (Character.isWhitespace(character)) {
            character = sourceString.charAt(linePosition);
            linePosition++;
        }

        if (character == '"') {
            return stringToken(sourceString);
        } else if (character == '.') {
            return directiveToken(sourceString);
        } else if (Character.isDigit(character) || character == '$' || character == '%') {
            return numberToken(sourceString, character);
        } else if (character == '\'') {
            return characterToken(sourceString);
        } else if (Character.isAlphabetic(character) || character == '_' || character == '@') {
            return labelToken(sourceString, character);
        } else if (operators.contains(character)) {
            return "" + character;
        }

        return null;
    }

    private String labelToken(String sourceString, char character) {

        StringBuilder builder = new StringBuilder();

        do {
            builder.append(character);

            if (linePosition >= sourceString.length()) {
                break;
            }

            character = sourceString.charAt(linePosition);
            linePosition++;

        } while (!Character.isWhitespace(character) && character != '.');

        if (character == '.') {
            linePosition--;
        }

        return builder.toString().trim();
    }

    private String characterToken(String sourceString) {
        char character = '\'';

        StringBuilder builder = new StringBuilder().append(character);

        if (linePosition >= sourceString.length()) {
            throw new IllegalStateException("Unterminated character at " + sourceString + ":" + getCurrentLine());
        }
        character = sourceString.charAt(linePosition);
        linePosition++;
        builder.append(character);

        character = sourceString.charAt(linePosition);
        if (character != '\'') {
            throw new IllegalStateException("Unterminated character at " + sourceString + ":" + getCurrentLine());
        }
        linePosition++;
        builder.append(character);


        return builder.toString().trim();

    }

    private String numberToken(String sourceString, char character) {
        var chars = new Character[]{'A', 'B', 'C', 'D', 'E', 'F', 'H','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'h', '.', '$', '%'};
        final List<Character> allowedCharacters = Arrays.asList(chars);
        StringBuilder builder = new StringBuilder().append(character);

        do {

            if (!allowedCharacters.contains(character)) {
                linePosition--;
                break;
            }

            if (linePosition >= sourceString.length()) {
                break;
            }
            character = sourceString.charAt(linePosition);
            linePosition++;
            if (character == '.') {
                //Handle optional size
                if (!Character.isDigit(sourceString.charAt(linePosition))) {//character is not a digit, may be size.
                    linePosition--;
                    return builder.toString().trim();
                }
            }
            if (!allowedCharacters.contains(character)) {
                linePosition--;
                break;
            }
            builder.append(character);
        } while (allowedCharacters.contains(character));

        return builder.toString().trim();
    }

    private String directiveToken(String sourceString) {
        StringBuilder builder = new StringBuilder().append(".");
        if (!(Character.isAlphabetic(sourceString.charAt(linePosition)) || Character.isDigit(sourceString.charAt(linePosition)))) {
            //TODO: Real Error Handling
            throw new IllegalStateException("Empty directive at " + sourceString);
        }
        char character;
        do {
            if (linePosition >= sourceString.length()) {
                break;
            }
            character = sourceString.charAt(linePosition);
            linePosition++;
            builder.append(character);
        } while (Character.isAlphabetic(character) || Character.isDigit(character));
        return builder.toString().trim();

    }

    private String stringToken(String sourceString) {
        StringBuilder builder = new StringBuilder().append("\"");
        char character;
        do {
            if (linePosition >= sourceString.length()) {
                //TODO: Real error handling.
                throw new IllegalStateException("Unterminated String " + sourceString);
            }
            character = sourceString.charAt(linePosition);
            linePosition++;
            builder.append(character);
        } while (character != '"');
        return builder.toString();
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


    public boolean endOfInput() {
        var currentLine = getCurrentLine();
        if (linePosition >= currentLine.getDataLine().length()) {
            return lineNumber >= source.lineCount();
        }
        return lineNumber > source.lineCount();
    }
}
