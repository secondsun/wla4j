package net.sagaoftherealms.tools.snes.assembler.main;

import net.sagaoftherealms.tools.snes.assembler.ActiveFileInfo;
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

    public static final String FILE_END_MARK = ((char) 0xA) + ".E ";

    CharBuffer buffer = CharBuffer.allocate(0);
    final Flags flags;


    private String defaultIncludeDirectory = "." + File.pathSeparator;

    private LinkedList<ActiveFileInfo> activeFileInfoList = new LinkedList<>();

    private static int file_name_id = 1;
    private static int size = 0;

    private static int open_files = 0;

    public InputData(Flags flags) {
        this.flags = flags;
    }

    public void includeFile(InputStream fileStream, String fileName) {
        int file_size;

        if (flags.isExtraDefinitions()) {
            flags.redefine("WLA_FILENAME", 0.0, fileName, DEFINITION_TYPE_STRING);
            flags.redefine("wla_filename", 0.0, fileName, DEFINITION_TYPE_STRING);
        }

        ActiveFileInfo currentFileInfo = new ActiveFileInfo();

        activeFileInfoList.add(currentFileInfo);

        currentFileInfo.line_current = 1;
        /* name */
        currentFileInfo.filename_id = file_name_id;
        file_name_id++;

        String fileContents = null;

        try {
            fileContents = IOUtils.toString(fileStream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        file_size = fileContents.length();
        if (buffer.capacity() == 0) {

            StringBuilder fileBuilder = new StringBuilder();

            /* preprocess */
            preprocess_file(fileContents, fileBuilder, fileName);
            String preprocessedFile = fileBuilder.toString();
            buffer = CharBuffer.allocate(preprocessedFile.length() + 4);
            buffer.append(preprocessedFile);

            buffer.append(FILE_END_MARK);

            open_files++;
            
            return;
        } else {
            /**
             tmp_b = malloc(sizeof( char) *(size + file_size + 4));
             if (tmp_b == NULL) {
             sprintf(emsg, "Out of memory while trying to expand the project to incorporate file \"%s\".\n", full_name);
             print_error(emsg, ERROR_INC);
             return FAILED;
             }

             //         /* reallocate tmp_a
             if (tmp_a_size < file_size + 4) {
             if (tmp_a != NULL)
             free(tmp_a);

             tmp_a = malloc(sizeof( char) *(file_size + 4));
             if (tmp_a == NULL) {
             sprintf(emsg, "Out of memory while allocating new room for \"%s\".\n", full_name);
             print_error(emsg, ERROR_INC);
             return FAILED;
             }

             tmp_a_size = file_size + 4;
             }

             // preprocess
             inz = 0;
             preprocess_file(include_in_tmp, include_in_tmp + file_size, tmp_a, & inz, full_name);

             tmp_a[inz++] = 0xA;
             tmp_a[inz++] = '.';
             tmp_a[inz++] = 'E';
             tmp_a[inz++] = ' ';

             open_files++;

             memcpy(tmp_b, buffer, i);
             memcpy(tmp_b + i, tmp_a, inz);
             memcpy(tmp_b + i + inz, buffer + i, size - i);

             free(buffer);

             size += inz;
             buffer = tmp_b;
             */
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

        /* if failed then try to find the file in the current directory */
        if (!f.isFile() || !f.exists()) {
            if (activeFileInfoList.getLast() != null) {
                System.err.println(String.format("%s:%d: ", activeFileInfoList.getLast().filename, activeFileInfoList.getLast().line_current));
            }

            throw new RuntimeException(String.format("Error opening file \"%s\".\n", fullName));
        }

        includeFile(new FileInputStream(f), fullName);

    }

    private String get_file_name(int id) {
        if (activeFileInfoList.get(id) != null) {
            return activeFileInfoList.get(id).filename;
        } else {
            return null;
        }
    }

    private static String createFullName(String path, String fileName) {
        return path + fileName;
    }

    /* the mystery preprocessor - touch it and prepare for trouble ;) the preprocessor
  removes as much white space as possible from the source file. this is to make
  the parsing of the file, that follows, simpler. */
    private static void preprocess_file(String inputString, StringBuilder out_buffer, String file_name) {

        /* this is set to 1 when the parser finds a non white space symbol on the line it's parsing */
        int got_chars_on_line = 0;

      /* values for z - z tells us the state of the preprocessor on the line it is processing
         the value of z is 0 at the beginning of a new line, and can only grow: 0 -> 1 -> 2 -> 3
         0 - new line
         1 - 1+ characters on the line
         2 - extra white space removed
         3 - again 1+ characters follow */
        int z = 0;


        int square_bracket_open = 0;

        char inputArray[] = inputString.toCharArray();
        int input_end = inputArray.length;

        for (int input = 0; input < inputArray.length; ) {
            char inputTest = inputArray[input];
            switch (inputTest) {
                case ';':
                    /* clear a commented line */
                    input++;
                    for (; input < input_end && inputArray[input] != 0x0A && inputArray[input] != 0x0D;
                         input++) {
                    }

                    break;
                case '*':
                    if (got_chars_on_line == 0) {
                        /* clear a commented line */
                        for (; input < input_end && inputArray[input] != 0x0A && inputArray[input] != 0x0D;
                             input++) {
                        }

                    } else {
                        /* multiplication! */
                        input++;
                        out_buffer.append("*");
                    }
                    break;
                case '/':
                    if (inputArray[input + 1] == '*') {
                        /* remove an ANSI C -style block comment */
                        got_chars_on_line = 0;
                        input += 2;
                        while (got_chars_on_line == 0) {
                            for (; input < input_end && inputArray[input] != '/' && inputArray[input] != 0x0A;
                                 input++)
                                ;
                            if (input >= input_end) {
                                throw new RuntimeException(String.format("Comment wasn't terminated properly in file \"%s\".\n", file_name));

                            }
                            if (inputArray[input] == 0x0A) {
                                out_buffer.append((char) 0x0A);
                            }

                            if (inputArray[input] == '/' && inputArray[input - 1] == '*') {
                                got_chars_on_line = 1;
                            }
                            input++;
                        }

                    } else {
                        input++;
                        out_buffer.append("/");
                        got_chars_on_line = 1;
                    }
                    break;
                case ':':
                    /* finding a label resets the counters */
                    input++;
                    out_buffer.append(":");
                    got_chars_on_line = 0;
                    break;
                case 0x09:
                case ' ':
                    /* remove extra white space */
                    input++;
                    out_buffer.append(' ');

                    for (; input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09); input++) {

                    }

                    got_chars_on_line = 1;
                    if (z == 1)
                        z = 2;
                    break;
                case 0x0A:
                    /* take away white space from the end of the line */
                    input++;

                    if (out_buffer.length() > 0 ) {
                        int endIndex = out_buffer.length() - 1;

                        while (out_buffer.charAt(endIndex) == ' ' && endIndex >= 0) {
                            out_buffer.deleteCharAt(endIndex);
                            endIndex = out_buffer.length() - 1;
                        }
                    }
                    out_buffer.append((char) 0x0A);

                    /* moving on to a new line */
                    got_chars_on_line = 0;
                    z = 0;
                    square_bracket_open = 0;

                    break;
                case 0x0D:
                    input++;
                    break;
                case '\'':
                    if (inputArray[input + 2] == '\'') {
                        out_buffer.append('\'');
                        input++;
                        out_buffer.append(inputArray[input]);

                        input++;

                        out_buffer.append('\'');
                        input++;

                    } else {
                        out_buffer.append('\'');
                        input++;

                    }
                    got_chars_on_line = 1;
                    break;
                case '"':
                    /* don't touch strings */
                    out_buffer.append('"');
                    input++;

                    got_chars_on_line = 1;
                    while (true) {
                        for (; input < input_end && inputArray[input] != '"' && inputArray[input] != 0x0A && inputArray[input] != 0x0D; ) {
                            out_buffer.append(inputArray[input]);
                            input++;

                        }

                        if (input >= input_end)
                            break;
                        else if (inputArray[input] == 0x0A || inputArray[input] == 0x0D) {
                            /* process 0x0A/0x0D as usual, and later when we try to input a string, the parser will fail as 0x0A comes before a " */
                            break;
                        } else if (inputArray[input] == '"' && inputArray[input - 1] != '\\') {
                            out_buffer.append('"');
                            input++;

                            break;
                        } else {
                            out_buffer.append('"');
                            input++;
                        }
                    }
                    break;

                case '(':
                    out_buffer.append('(');
                    input++;

                    for (; input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09); input++) {
                    }
                    got_chars_on_line = 1;
                    break;

                case ')':
                    /* go back? */
                    if (got_chars_on_line == 1 && out_buffer.charAt(out_buffer.length() - 1) == ' ') {
                        out_buffer.deleteCharAt(out_buffer.length() - 1);
                    }
                    out_buffer.append(')');
                    input++;
                    got_chars_on_line = 1;
                    break;

                case '[':
                    out_buffer.append(inputArray[input]);
                    input++;
                    got_chars_on_line = 1;
                    square_bracket_open = 1;
                    break;

                case ',':
                case '+':
                case '-':
                    if (got_chars_on_line == 0) {
                        for (; input < input_end && (inputArray[input] == '+' || inputArray[input] == '-'); input++) {
                            out_buffer.append(inputArray[input]);
                        }
                        got_chars_on_line = 1;
                    } else {

                        /* go back? */
                        if (out_buffer.charAt(out_buffer.length() - 1) == ' ' && square_bracket_open == 1) {
                            out_buffer.deleteCharAt(out_buffer.length() - 1);
                        }
                        out_buffer.append(inputArray[input]);
                        input++;
                        for (; input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09); input++) {
                        }
                        got_chars_on_line = 1;
                    }
                    break;
                default:
                    out_buffer.append(inputArray[input]);
                    input++;

                    got_chars_on_line = 1;

                    /* mode changes... */
                    if (z == 0)
                        z = 1;
                    else if (z == 2)
                        z = 3;
                    break;
            }
        }

        return;
    }


}
