package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.token.Token;

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
            return createStringToken(sourceString);
        } else if (character == '.') {
            return directiveToken();
        }
        
        return null;
    }

    private String directiveToken() {
        return ".IF";
    }

    private String createStringToken(String sourceString) {
        StringBuilder builder = new StringBuilder().append("\"");
        char character;
        do {
            if (linePosition >= sourceString.length() ) {
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
