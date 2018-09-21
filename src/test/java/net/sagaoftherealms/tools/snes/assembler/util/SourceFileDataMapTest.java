package net.sagaoftherealms.tools.snes.assembler.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SourceFileDataMapTest {

    @Test
    public void basicAdd() {
        SourceFileDataMap datamap = new SourceFileDataMap();
        datamap.addLine("testfile.s", 1, ".INCLUDE \"defines.i\"");
        datamap.addLine("testfile.s", 2, ".DEFINE TESA $fe");

        SourceDataLine line = new SourceDataLine();
        line.setDataLine(".DEFINE TESA $fe");
        line.setFileName("testfile.s");
        line.setSourceLineNumber(2);

        Assert.assertEquals(line, datamap.getLine(2));
    }

    @Test
    public void emptyFile() {
        SourceFileDataMap datamap = new SourceFileDataMap();
        Assert.assertTrue(datamap.isEmpty());

        datamap.addLine("testfile.s", 1, ".INCLUDE \"defines.i\"");
        datamap.addLine("testfile.s", 2, ".DEFINE TESA $fe");

        Assert.assertFalse(datamap.isEmpty());


    }

}
