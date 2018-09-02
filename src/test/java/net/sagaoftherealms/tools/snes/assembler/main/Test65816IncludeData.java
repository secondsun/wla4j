package net.sagaoftherealms.tools.snes.assembler.main;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class Test65816IncludeData {

    @Test
    public void basicInput() throws IOException {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"), "main.s");
        data.buffer.rewind();

        String expectedOutput = IOUtils.toString(Test65816IncludeData.class.getClassLoader().getResourceAsStream("preprocess.out"), "UTF-8") + InputData.FILE_END_MARK;
        assertEquals(expectedOutput, data.buffer.toString());
    }

}
