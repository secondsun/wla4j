package net.sagaoftherealms.tools.snes.assembler.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.util.SourceFileDataMap;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;
import org.apache.commons.io.IOUtils;

/**
 * This class is the "object" which is all of the input files. It is mutable and has a few
 * convenience functions for the parsers.
 */
public class InputData {

  private String defaultIncludeDirectory = "." + File.pathSeparator;

  private SourceFileDataMap combinedSourceFile = new SourceFileDataMap();

  public InputData() {}

  public void includeFile(InputStream fileStream, String fileName, int includeAt) {

    String fileContents = null;

    try {
      fileContents = IOUtils.toString(fileStream, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    /* preprocess */
    SourceFileDataMap preprocessedDataMap = preprocess_file(fileContents, fileName);
    combinedSourceFile.addMapAt(preprocessedDataMap, includeAt);
  }

  /* the mystery preprocessor - touch it and prepare for trouble ;) the preprocessor
  removes as much white space as possible from the source file. this is to make
  the parsing of the file, that follows, simpler. */
  private SourceFileDataMap preprocess_file(String inputString, String file_name) {

    SourceFileDataMap buffer = new SourceFileDataMap();
    String[] lines = inputString.split("\n");
    for (int index = 0; index < lines.length; index++) {
      buffer.addLine(file_name,index, lines[index]);
    }
    return buffer;
  }

  /** Pretty prints the processed source */
  public String prettyPrint() {
    return combinedSourceFile.toString();
  }

  /**
   * Creates a scanner that uses a provide table of opcodes for the OpCode token type.
   *
   * @param opTable Array of opcodes
   * @return a scanner which tokenizes
   */
  public SourceScanner startRead(OpCode[] opTable) {
    return new SourceScanner(combinedSourceFile, opTable);
  }
}
