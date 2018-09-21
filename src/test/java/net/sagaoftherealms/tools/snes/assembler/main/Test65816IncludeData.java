package net.sagaoftherealms.tools.snes.assembler.main;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Test65816IncludeData {

    @Test
    public void basicInclude() throws IOException {
        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"), "main.s",0);

        String expectedOutput = IOUtils.toString(Test65816IncludeData.class.getClassLoader().getResourceAsStream("preprocess.out"), "UTF-8");
        assertEquals(expectedOutput, data.prettyPrint());
    }

    /**
     * This test will manually set the buffers position to a location that the parser would while it was assembling multiple files .
     * @throws IOException
     */
    @Test
    public void multiInclude() throws IOException {

        InputData data = new InputData(new Flags(" test.out "));
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("main.s"), "main.s", 0);
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("defines.i"), "defines.i", 1);
        data.includeFile(Test65816IncludeData.class.getClassLoader().getResourceAsStream("snes_memory.i"), "snes_memeory.i",2);

        String expectedOutput = IOUtils.toString(Test65816IncludeData.class.getClassLoader().getResourceAsStream("multiinclude.out"), "UTF-8");
        assertEquals(expectedOutput, data.prettyPrint());
    }


}
