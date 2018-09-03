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
    public void basicInclude() throws IOException {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"), "main.s");
        data.buffer.rewind();

        String expectedOutput = IOUtils.toString(Test65816IncludeData.class.getClassLoader().getResourceAsStream("preprocess.out"), "UTF-8") + InputData.FILE_END_MARK;
        assertEquals(expectedOutput, data.buffer.toString());
    }

    /**
     * This test will manually set the buffers position to a location that the parser would while it was assembling multiple files .
     * @throws IOException
     */
    @Test
    public void multiInclude() throws IOException {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"), "main.s");
        data.buffer.rewind();
        data.buffer.position(26);
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("defines.i"), "defines.i");
        data.buffer.rewind();
        data.buffer.position(55);
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("snes_memory.i"), "snes_memeory.i");
        data.buffer.rewind();

        String expectedOutput = IOUtils.toString(Test65816IncludeData.class.getClassLoader().getResourceAsStream("multiinclude.out"), "UTF-8");
        assertEquals(expectedOutput, data.buffer.toString());
    }


}
