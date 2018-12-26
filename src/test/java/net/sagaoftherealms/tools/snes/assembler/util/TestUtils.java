package net.sagaoftherealms.tools.snes.assembler.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import org.apache.commons.io.IOUtils;

public class TestUtils {

  /**
   * Converts a string to a stream
   *
   * @param inputString a string
   * @return a stream containing inputString
   */
  public static InputStream $(String inputString) {
    return IOUtils.toInputStream(inputString, Charset.defaultCharset());
  }

  public static SourceScanner toScanner(String sourceLine) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());
    return scanner;
  }
}
