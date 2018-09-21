package net.sagaoftherealms.tools.snes.assembler.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This file is the the {@link net.sagaoftherealms.tools.snes.assembler.main.InputData} data and mapping to the original source files.
 *
 * The way it works is InputData data contains the positioning and manipulating information and this file is the underlying characters and mapping back to the original file.
 *
 */
public class SourceFileDataMap {

    /**
     * 1:1 mapping of listEntry to lines in the source file.
     */
    private List<SourceDataLine> lines = new ArrayList<>();


    /**
     *
     * @param dataLineNumber the line in the combined data file
     * @return the line from the data file plus origin information.
     */
    public SourceDataLine getLine(int dataLineNumber) {
        return lines.get(dataLineNumber - 1);
    }

    public void addLine(String sourceFileName, int sourceLineNumber, String sourceCode) {
        lines.add(new SourceDataLine(sourceFileName, sourceLineNumber, sourceCode));
    }

    /**
     * if there are no lines, then the map is empty.
     * @return true if the map is empty, false otherwise.
     */
    public boolean isEmpty() {
        return lines.isEmpty();
    }
}
