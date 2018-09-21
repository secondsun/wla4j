package net.sagaoftherealms.tools.snes.assembler.main;

import net.sagaoftherealms.tools.snes.assembler.ActiveFileInfo;
import net.sagaoftherealms.tools.snes.assembler.util.SourceFileDataMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.LinkedList;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;

/**
 * This class is the "object" which is all of the input files.  It is mutable and has a few convenience functions
 * for the parsers.
 */
public class InputData {

    final Flags flags;
    private String defaultIncludeDirectory = "." + File.pathSeparator;

    private SourceFileDataMap buffer = new SourceFileDataMap();

    public InputData(Flags flags) {
        this.flags = flags;
    }

    public void includeFile(InputStream fileStream, String fileName) {

        if (flags.isExtraDefinitions()) {
            flags.redefine("WLA_FILENAME", 0.0, fileName, DEFINITION_TYPE_STRING);
            flags.redefine("wla_filename", 0.0, fileName, DEFINITION_TYPE_STRING);
        }


        String fileContents = null;

        try {
            fileContents = IOUtils.toString(fileStream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (buffer.isEmpty()) {

            /* preprocess */
            preprocess_file(fileContents, buffer, fileName);

            return;
        } else {

//            int position = buffer.position();
//            buffer.position(0);
//
//
//            StringBuilder fileBuilder = new StringBuilder();
//
//            preprocess_file(fileContents, fileBuilder, fileName);
//            CharBuffer newBuffer = CharBuffer.allocate(buffer.capacity() + fileBuilder.toString().length() + FILE_END_MARK.length());
//            fileBuilder.append(FILE_END_MARK);
//
//            open_files++;
//
//            newBuffer.append(buffer.subSequence(0, position).toString());
//            newBuffer.append(fileBuilder.toString());
//            newBuffer.append(buffer.subSequence(position, buffer.length()).toString());
//
//
//            size += newBuffer.length();
//            buffer = newBuffer;
//            buffer.position(position);
            return;
        }

    }

    public void includeFile(String name) throws IOException {

        File f;

        String includeDirectory, fullName;

        if (name == null) {
            name = "";
        }

        /* create the full output file name */
        if (flags.useExternalIncludesDirectory())
            includeDirectory = flags.getExternalIncludesDirectory();
        else
            includeDirectory = defaultIncludeDirectory;

        fullName = createFullName(includeDirectory, name);

        f = new File(fullName);

        if (!f.isFile() || !f.exists()) {

            throw new RuntimeException(String.format("Error opening file \"%s\".\n", fullName));
        }

        includeFile(new FileInputStream(f), fullName);

    }

    private static String createFullName(String path, String fileName) {
        return path + fileName;
    }

    /* the mystery preprocessor - touch it and prepare for trouble ;) the preprocessor
  removes as much white space as possible from the source file. this is to make
  the parsing of the file, that follows, simpler. */
    private static void preprocess_file(String inputString, SourceFileDataMap out_buffer, String file_name) {

        //Is the preprocessor consuming a C style /*..*/ multiline comment?
        boolean consumingMultiLineComment = false;

        /* this is set to 1 when the parser finds a non white space symbol on the line it's parsing */
        int got_chars_on_line = 0;

      /* values for z - z tells us the state of the preprocessor on the line it is processing
         the value of z is 0 at the beginning of a new line, and can only grow: 0 -> 1 -> 2 -> 3
         0 - new line
         1 - 1+ characters on the line
         2 - extra white space removed
         3 - again 1+ characters follow */
        int z = 0;

        int lineCount = 0;

        int square_bracket_open = 0;

        inputString = inputString.replace("\r\n", "\n"); //Turn windows line endings into unix line endings
        inputString = inputString.replace("\r", "\n"); // turn mac line endings into unix line endings
        String[] lines = inputString.split("\n"); // split into lines
        for (String line : lines) {
            lineCount++;


            //If we are consuming a comment continue or move along.
            //You might think "can't we just gobble up the lines"
            //we can, but I didn't want to keep the source lines updated in that
            //sorry not sorry
            if (consumingMultiLineComment) {
                if (line.contains("*/")) {
                    line = line.split("\\*/")[1];//Everything after the comment isn't a comment
                    consumingMultiLineComment = false;
                } else {
                    continue;
                }
            } else {
                if (line.startsWith("*")) {// * on as the first character is a line comment.
                    continue;
                }
            }

            char[] chars = line.toCharArray();
            StringBuilder lineBuilder = new StringBuilder();
            for (int index = 0; index < chars.length;index++) {
                boolean inString = false;
                //Handle multi line comments
                if (consumingMultiLineComment && !inString) {
                    if (chars[index] != '*') {
                        continue;
                    } else {
                        if (peekFor(chars, index+1,'/')) {
                            index++;
                            consumingMultiLineComment = false;
                        }
                    }
                }
            }

            line = line.split(";")[0];//Remove line comment

            if (line.contains("/*")) {
                consumingMultiLineComment = true;
                line = line.split("/\\*")[0];
            }

            line = line.trim(); //remove extra whitespace

            //Skip empty lines
            if (line.isEmpty()) {
                continue;
            }


        }
    }

    /**
     * This method will look for s at i in chars in a array safe way
     * @param chars source characters
     * @param i index to check
     * @param s character to check for
     * @return
     */
    private static boolean peekFor(char[] chars, int i, char s) {
        if (i >= chars.length) {
            return false;
        }
        return chars[i] == s;
    }

}
