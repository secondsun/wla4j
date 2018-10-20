package net.sagaoftherealms.tools.snes.assembler.util;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;

public class TestUtils {

    /**
     * Converts a string to a stream
     * @param inputString a string
     * @return a stream containing inputString
     */
    public static InputStream $(String inputString) {
        return IOUtils.toInputStream(inputString, Charset.defaultCharset());
    }
}
