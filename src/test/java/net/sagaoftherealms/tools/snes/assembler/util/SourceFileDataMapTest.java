package net.sagaoftherealms.tools.snes.assembler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Source File Metatadata is retained")
public class SourceFileDataMapTest {

  @Test
  public void basicAdd() {
    SourceFileDataMap datamap = new SourceFileDataMap();
    datamap.addLine("testfile.s", 1, ".INCLUDE \"defines.i\"");
    datamap.addLine("testfile.s", 2, ".DEFINE TESA $fe");

    SourceDataLine line = new SourceDataLine();
    line.setDataLine(".DEFINE TESA $fe");
    line.setFileName("testfile.s");
    line.setBeginSourceLineNumber(2);

    assertEquals(line, datamap.getLine(2));
  }

  @Test
  public void emptyFile() {
    SourceFileDataMap datamap = new SourceFileDataMap();
    assertTrue(datamap.isEmpty());

    datamap.addLine("testfile.s", 1, ".INCLUDE \"defines.i\"");
    datamap.addLine("testfile.s", 2, ".DEFINE TESA $fe");

    assertFalse(datamap.isEmpty());
  }
}
