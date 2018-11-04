package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtils {

    /**
     * Converts a string to a stream
     * @param inputString a string
     * @return a stream containing inputString
     */
    public static InputStream toStream(String inputString) {
        return IOUtils.toInputStream(inputString, Charset.defaultCharset());
    }

    public static SourceScanner toScanner( String sourceLine) {
        final String outfile = "test.out";
        final String inputFile = "test.s";
        final int lineNumber = 0;

        var data = new InputData(new Flags(outfile));
        data.includeFile(toStream(sourceLine), inputFile, lineNumber);

        var scanner = data.startRead(Opcodes65816.opt_table);
        return scanner;
    }

}
