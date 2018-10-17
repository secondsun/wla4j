package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.token.Token;

import java.util.Arrays;
import java.util.List;

/**
 * A stateful object that is used to read data from a {@link SourceFileDataMap}
 */
public class SourceScanner {
    private final SourceFileDataMap source;

    private int lineNumber = 0;
    private int linePosition = 0;

    public SourceScanner(SourceFileDataMap source) {
        this.source = source;
    }

    public SourceDataLine getNextLine() {
        return source.getLine(++lineNumber);
    }

    public SourceDataLine getCurrentLine() {
        return source.getLine(lineNumber);
    }


    public Token getNextToken() {
        String tokenString = getNextTokenString();
        return new Token(tokenString);
    }

    private String getNextTokenString() {

        if (lineNumber == 0) {
            getNextLine();
        }

        //get line where we left off reading
        var line = getCurrentLine();
        var sourceString = line.getDataLine();

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
        } else if (Character.isDigit(character) || character == '$'  || character == '%') {
            return numberToken(sourceString, character);
        } else if (character == '\'') {
            return characterToken(sourceString);
        }

        return null;
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
        var chars = new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'h', '.', '$', '%'};
        final List<Character> allowedCharacters = Arrays.asList(chars);
        StringBuilder builder = new StringBuilder().append(character);

        do {
            if (linePosition >= sourceString.length()) {
                break;
            }
            character = sourceString.charAt(linePosition);
            linePosition++;
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
}
