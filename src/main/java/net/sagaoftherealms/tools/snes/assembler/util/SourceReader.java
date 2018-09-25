package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.pass.Token;

/**
 * A stateful object that is used to read data from a {@link SourceFileDataMap}
 */
public class SourceReader {
    private final SourceFileDataMap source;

    private int lineNumber = 0;
    private int linePosition = 0;

    public SourceReader(SourceFileDataMap source) {
        this.source = source;
    }

    public SourceDataLine getNextLine() {
        return source.getLine(++lineNumber);
    }

    public SourceDataLine getCurrentLine() {
        return source.getLine(lineNumber);
    }


    public Token getNextToken() {
        return null;
    }
}
