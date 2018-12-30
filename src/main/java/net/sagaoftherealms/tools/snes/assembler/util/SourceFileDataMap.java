package net.sagaoftherealms.tools.snes.assembler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is the the {@link net.sagaoftherealms.tools.snes.assembler.main.InputData} data and
 * mapping to the original source files.
 *
 * <p>The way it works is InputData data contains the positioning and manipulating information and
 * this file is the underlying characters and mapping back to the original file.
 */
public class SourceFileDataMap {

  /** 1:1 mapping of listEntry to lines in the source file. */
  private List<SourceDataLine> lines = new ArrayList<>();

  /**
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
   *
   * @return true if the map is empty, false otherwise.
   */
  public boolean isEmpty() {
    return lines.isEmpty();
  }

  /**
   * Adds a character to the last line of the file.
   *
   * @param s to add. Ignored if a newline.
   */
  public void append(char s) {
    if (s != '\n') {
      SourceDataLine line = lines.get(lines.size() - 1);
      if (line == null) {
        throw new IllegalStateException("You must call addLine before append.");
      } else {
        line.append(s);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    lines
        .stream()
        .filter(it -> !it.getDataLine().isEmpty())
        .forEach(it -> builder.append(it.getDataLine()).append(System.lineSeparator()));
    return builder.toString();
  }

  /**
   * Adds a new file starting on line includeAt
   *
   * @param preprocessedDataMap preprocessed file
   * @param includeAt line to add at.
   */
  public void addMapAt(SourceFileDataMap preprocessedDataMap, int includeAt) {
    lines.addAll(includeAt, preprocessedDataMap.lines);
  }


  public int lineCount() {
    return lines.size();
  }
}
