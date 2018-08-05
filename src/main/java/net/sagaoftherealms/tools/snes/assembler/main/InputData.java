package net.sagaoftherealms.tools.snes.assembler.main;

import net.sagaoftherealms.tools.snes.assembler.ActiveFileInfo;
import net.sagaoftherealms.tools.snes.assembler.FileNameInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.FAILED;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.SUCCEEDED;

/**
 * This class is the "object" which is all of the input files.  It is mutable and has a few convenience functions
 * for the parsers.
 */
public class InputData {

    CharBuffer buffer = CharBuffer.allocate(0);
    final Flags flags;


    private FileNameInfo file_name_info, file_name_info_first;
    private String include_dir;

    private ActiveFileInfo active_file_info_last;
    private ActiveFileInfo active_file_info_first;
    private FileNameInfo file_name_info_last;
    private static int file_name_id = 1;
    private static int size = 0;

    private static int open_files = 0;

    public InputData(Flags flags) {
        this.flags = flags;
    }

    public void includeFile(String name) throws IOException {
        int file_size;
        int id;
        String tmp_b, n;
        File f;

        String tmp_c, full_name;


        /* create the full output file name */
        if (flags.isUseExtIncDir())
            tmp_c = flags.getExtIncDir();
        else
            tmp_c = include_dir;

        full_name = create_full_name(tmp_c, name);

        f = new File(full_name);
        id = 0;

        if ((!f.isFile() || !f.exists()) && (tmp_c == null || tmp_c.isEmpty())) {
            throw new RuntimeException(String.format("Error opening file \"%s\".", name));
        }

        /* if not found in ext_incdir silently try the include directory */
        if ((!f.isFile() || !f.exists()) && flags.isUseExtIncDir()) {
            full_name = create_full_name(include_dir, name);

            f = new File(full_name);
            id = 0;

            if (f == null && (include_dir == null || include_dir.isEmpty())) {
                throw new RuntimeException(String.format("Error opening file \"%s\".", name));
            }
        }

        /* if failed try to find the file in the current directory */
        if (!f.isFile() || !f.exists()) {
            System.err.println(String.format("%s:%d: ", get_file_name(active_file_info_last.filename_id), active_file_info_last.line_current));
            System.err.println(String.format("INCLUDE_FILE: Could not open \"%s\", trying \"%s\"... ", full_name, name));
            f = new File(name);
            id = 1;
        }

        if (f == null) {
            throw new RuntimeException(String.format("Error opening file \"%s\".\n", full_name))
        }

        if (id == 1) {
            full_name = name;
        }


        if (flags.isExtraDefinitions()) {
            flags.redefine("WLA_FILENAME", 0.0, name, DEFINITION_TYPE_STRING);
            flags.redefine("wla_filename", 0.0, name, DEFINITION_TYPE_STRING);
        }

        file_size = (int) f.length();

        ActiveFileInfo active_file_info_tmp = new ActiveFileInfo();


        if (active_file_info_first == null) {
            active_file_info_first = active_file_info_tmp;
            active_file_info_last = active_file_info_tmp;
            active_file_info_tmp.prev = null;
        } else {
            active_file_info_tmp.prev = active_file_info_last;
            active_file_info_last.next = active_file_info_tmp;
            active_file_info_last = active_file_info_tmp;
        }

        active_file_info_tmp.line_current = 1;

        /* name */
        FileNameInfo file_name_info_tmp = file_name_info_first;
        id = 0;
        while (file_name_info_tmp != null) {
            if (file_name_info_tmp.getFileName().equals(full_name)) {
                id = file_name_info_tmp.getId();
                active_file_info_tmp.filename_id = id;
                break;
            }
            file_name_info_tmp = file_name_info_tmp.getNext();
        }

        if (id == 0) {
            file_name_info_tmp = new FileNameInfo();
            n = full_name;

            if (file_name_info_first == null) {
                file_name_info_first = file_name_info_tmp;
                file_name_info_last = file_name_info_tmp;
            } else {
                file_name_info_last.setNext(file_name_info_tmp);
                file_name_info_last = file_name_info_tmp;
            }


            file_name_info_tmp.fileName = n;
            active_file_info_tmp.filename_id = file_name_id;
            file_name_info_tmp.id = file_name_id;
            file_name_id++;
        }

        CharBuffer include_in_tmp = CharBuffer.wrap(FileUtils.readFileToString(f, "UTF-8"));


        if (buffer.capacity() == 0) {
            buffer = CharBuffer.allocate(file_size + 4);


            /* preprocess */
            preprocess_file(include_in_tmp, buffer, full_name);

            buffer.append((char)0xA);
            buffer.append('.');
            buffer.append('E');
            buffer.append(' ');

            open_files++;
            return;
        } else {

            tmp_b = malloc(sizeof( char) *(size + file_size + 4));
            if (tmp_b == NULL) {
                sprintf(emsg, "Out of memory while trying to expand the project to incorporate file \"%s\".\n", full_name);
                print_error(emsg, ERROR_INC);
                return FAILED;
            }

            /* reallocate tmp_a */
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

            /* preprocess */
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

            return;
        }
    }

    private static String get_file_name(int id) {


        FileNameInfo fni = file_name_info_first;
        while (fni != null) {
            if (id == fni.getId())
                return fni.getFileName();
            fni = fni.getNext();
        }

    }

    private static String create_full_name(String tmp_c, String name) {
        tmp_c = tmp_c == null ? ("." + File.pathSeparator) : tmp_c;
        name = name == null ? "" : name;
        return tmp_c + name;
    }

    /* the mystery preprocessor - touch it and prepare for trouble ;) the preprocessor
  removes as much white space as possible from the source file. this is to make
  the parsing of the file, that follows, simpler. */
    private static void preprocess_file(CharBuffer inputString, CharBuffer out_buffer, String file_name) {

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
        int inputIndex = 0;
        int outputIndex = 0;

        char *output = out_buffer;


        for (char inputTest : inputString.toCharArray()) {
            switch (inputTest){
                case ';':
                    /* clear a commented line */
                    input++;
                    for (; input < input_end && *input != 0x0A && *input != 0x0D;
                    input++)
                    ;
                    output--;
                    for (; output > out_buffer && *output == ' ';output--)
                    ;
                    if (output < out_buffer)
                        output = out_buffer;
                    else if (*output != ' ')
                    output++;
                    break;
                case '*':
                    if (got_chars_on_line == 0) {
                        /* clear a commented line */
                        input++;
                        for (; input < input_end && *input != 0x0A && *input != 0x0D;
                        input++)
                        ;
                    } else {
                        /* multiplication! */
                        input++;
	                    *output = '*';
                        output++;
                    }
                    break;
                case '/':
                    if (*(input + 1) == '*'){
                    /* remove an ANSI C -style block comment */
                    got_chars_on_line = 0;
                    input += 2;
                    while (got_chars_on_line == 0) {
                        for (; input < input_end && *input != '/' && *input != 0x0A;
                        input++)
                        ;
                        if (input >= input_end) {
                            sprintf(emsg, "Comment wasn't terminated properly in file \"%s\".\n", file_name);
                            print_error(emsg, ERROR_INC);
                            return FAILED;
                        }
                        if (*input == 0x0A){
	                        *output = 0x0A;
                            output++;
                        }
                        if (*input == '/' && *(input - 1) == '*'){
                            got_chars_on_line = 1;
                        }
                        input++;
                    }
                    output--;
                    for (; output > out_buffer && *output == ' ';
                    output--)
                    ;
                    if (output < out_buffer)
                        output = out_buffer;
                    else if (*output != ' ')
                    output++;
                } else {
                    input++;
	                *output = '/';
                    output++;
                    got_chars_on_line = 1;
                }
                break;
                case ':':
                    /* finding a label resets the counters */
                    input++;
                    *output = ':';
                    output++;
                    got_chars_on_line = 0;
                    break;
                case 0x09:
                case ' ':
                    /* remove extra white space */
                    input++;
                    *output = ' ';
                    output++;
                    for (; input < input_end && ( * input == ' ' || *input == 0x09);
                    input++)
                    ;
                    got_chars_on_line = 1;
                    if (z == 1)
                        z = 2;
                    break;
                case 0x0A:
                    /* take away white space from the end of the line */
                    input++;
                    output--;
                    for (; output > out_buffer && *output == ' '; output--);
                    output++;
                    *output = 0x0A;
                    output++;
                    /* moving on to a new line */
                    got_chars_on_line = 0;
                    z = 0;
                    square_bracket_open = 0;

                    break;
                case 0x0D:
                    input++;
                    break;
                case '\'':
                    if (*(input + 2) == '\'') {
	                *output = '\'';
                    input++;
                    output++;
	                *output = *input;
                    input++;
                    output++;
                    *output = '\'';
                    input++;
                    output++;
                } else {
	                *output = '\'';
                    input++;
                    output++;
                }
                got_chars_on_line = 1;
                break;
                case '"':
                    /* don't touch strings */
                    *output = '"';
                    input++;
                    output++;
                    got_chars_on_line = 1;
                    while (1) {
                        for (; input < input_end && *input != '"' && *input != 0x0A && *input != 0x0D; ) {
	                        *output = *input;
                            input++;
                            output++;
                        }

                        if (input >= input_end)
                            break;
                        else if (*input == 0x0A || *input == 0x0D) {
                            /* process 0x0A/0x0D as usual, and later when we try to input a string, the parser will fail as 0x0A comes before a " */
                            break;
                        } else if (*input == '"' && *(input - 1) != '\\') {
	                        *output = '"';
                            input++;
                            output++;
                            break;
                        } else {
	                        *output = '"';
                            input++;
                            output++;
                        }
                    }
                    break;

                case '(':
                    *output = '(';
                    input++;
                    output++;
                    for (; input < input_end && ( * input == ' ' || *input == 0x09);
                    input++)
                    ;
                    got_chars_on_line = 1;
                    break;

                case ')':
                    /* go back? */
                    if (got_chars_on_line == 1 && *(output - 1) == ' ')
                    output--;
                    *output = ')';
                    input++;
                    output++;
                    got_chars_on_line = 1;
                    break;

                case '[':
                    *output = *input;
                    input++;
                    output++;
                    got_chars_on_line = 1;
                    square_bracket_open = 1;
                    break;

                case ',':
                case '+':
                case '-':
                    if (got_chars_on_line == 0) {
                        for (; input < input_end && ( * input == '+' || *input == '-'); input++, output++){
	                        *output = *input;
                        }
                        got_chars_on_line = 1;
                    } else {

                        /* go back? */
                        if (*(output - 1) == ' ' && square_bracket_open == 1){
                            output--;
                        }
                        *output = *input;
                        input++;
                        output++;
                        for (; input < input_end && ( * input == ' ' || *input == 0x09); input++);
                        got_chars_on_line = 1;
                    }
                    break;
                default:
                    *output = *input;
                    input++;
                    output++;
                    got_chars_on_line = 1;

                    /* mode changes... */
                    if (z == 0)
                        z = 1;
                    else if (z == 2)
                        z = 3;
                    break;
            }
        }

        *out_size = output - out_buffer;

        return;
    }


}
