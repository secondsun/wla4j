
package net.sagaoftherealms.tools.snes.assembler;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_NONE;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_OBJECT;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.FAILED;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.SUCCEEDED;

/**
 * @author summers
 */
public class Main65816 {
    private static final String wla_version = "65816 Macro Assembler for Java based on WLA 65816 Macro Assembler v9.8a.";
    private static HashMap defines_map;
    private static HashMap global_unique_label_map;
    private static HashMap namespace_map;
    private static FileNameInfo file_name_info, file_name_info_first;
    private static Flags flags;
    private static String finalName;
    private static File gba_tmp_file;
    private static String include_dir;
    private static boolean commandline_parsing;
    private static ActiveFileInfo active_file_info_last;

    public static void main(String... args) {
        try {
            defines_map = new HashMap();
            global_unique_label_map = new HashMap();
            namespace_map = new HashMap();
            flags = new Flags(args);

            if (flags.getOutputFormat() == OUTPUT_NONE) {
                if (flags.getResult() == SUCCEEDED) {
                    /* assume object file output name */
                    flags.setOutputFormat(OUTPUT_OBJECT);
                    finalName = flags.getAsmName() + ".o";
                }
            }

            if (flags.getOutputFormat() == OUTPUT_NONE || flags.getResult() == FAILED) {
                printDefaultMessage();
                return;
            }

            if (flags.getAsmName().equals(finalName)) {
                throw new RuntimeException("Input and output files have same name");
            }

            gba_tmp_file = File.createTempFile("wla", "tmp");


            /* small inits */
            if (flags.isExtraDefinitions())
                generate_extra_definitions();

            commandline_parsing = false;

            /* start the process */
            include_file(flags.getAsmName());

            if (pass_1() == FAILED)

            if (pass_2() == FAILED)
                return 1;
            if (pass_3() == FAILED)
                return 1;
            if (flags.listfile_data == YES) {
                if (listfile_collect() == FAILED)
                    return 1;
            }
            if (pass_4() == FAILED)
                return 1;

            return 0;
        

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            proceduresAtExit();
        }
    }

    private static void include_file(String name) {
        
        int file_size, id;
        char *tmp_b, *n;
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
        if ((!f.isFile() || !f.exists()) && flags.isUseExtIncDir() ) {
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

        fseek(f, 0, SEEK_END);
        file_size = ftell(f);
        fseek(f, 0, SEEK_SET);

        active_file_info_tmp = malloc(sizeof(struct active_file_info));
        if (active_file_info_tmp == NULL) {
            sprintf(emsg, "Out of memory while trying allocate error tracking data structure for file \"%s\".\n", full_name);
            print_error(emsg, ERROR_INC);
            return FAILED;
        }
        active_file_info_tmp->next = NULL;

        if (active_file_info_first == NULL) {
            active_file_info_first = active_file_info_tmp;
            active_file_info_last = active_file_info_tmp;
            active_file_info_tmp->prev = NULL;
        }
        else {
            active_file_info_tmp->prev = active_file_info_last;
            active_file_info_last->next = active_file_info_tmp;
            active_file_info_last = active_file_info_tmp;
        }

        active_file_info_tmp->line_current = 1;

        /* name */
        file_name_info_tmp = file_name_info_first;
        id = 0;
        while (file_name_info_tmp != NULL) {
            if (strcmp(file_name_info_tmp->name, full_name) == 0) {
                id = file_name_info_tmp->id;
                active_file_info_tmp->filename_id = id;
                break;
            }
            file_name_info_tmp = file_name_info_tmp->next;
        }

        if (id == 0) {
            file_name_info_tmp = malloc(sizeof(struct file_name_info));
            n = malloc(strlen(full_name)+1);
            if (file_name_info_tmp == NULL || n == NULL) {
                if (file_name_info_tmp != NULL)
                    free(file_name_info_tmp);
                if (n != NULL)
                    free(n);
                sprintf(emsg, "Out of memory while trying allocate info structure for file \"%s\".\n", full_name);
                print_error(emsg, ERROR_INC);
                return FAILED;
            }
            file_name_info_tmp->next = NULL;

            if (file_name_info_first == NULL) {
                file_name_info_first = file_name_info_tmp;
                file_name_info_last = file_name_info_tmp;
            }
            else {
                file_name_info_last->next = file_name_info_tmp;
                file_name_info_last = file_name_info_tmp;
            }

            strcpy(n, full_name);
            file_name_info_tmp->name = n;
            active_file_info_tmp->filename_id = file_name_id;
            file_name_info_tmp->id = file_name_id;
            file_name_id++;
        }

        /* reallocate buffer */
        if (include_in_tmp_size < file_size) {
            if (include_in_tmp != NULL)
                free(include_in_tmp);

            include_in_tmp = malloc(sizeof(char) * file_size);
            if (include_in_tmp == NULL) {
                sprintf(emsg, "Out of memory while trying to allocate room for \"%s\".\n", full_name);
                print_error(emsg, ERROR_INC);
                return FAILED;
            }

            include_in_tmp_size = file_size;
        }

        /* read the whole file into a buffer */
        fread(include_in_tmp, 1, file_size, f);
        fclose(f);

        if (size == 0) {
            buffer = malloc(sizeof(char) * (file_size + 4));
            if (buffer == NULL) {
                sprintf(emsg, "Out of memory while trying to allocate room for \"%s\".\n", full_name);
                print_error(emsg, ERROR_INC);
                return FAILED;
            }

            /* preprocess */
            preprocess_file(include_in_tmp, include_in_tmp + file_size, buffer, &size, full_name);

            buffer[size++] = 0xA;
            buffer[size++] = '.';
            buffer[size++] = 'E';
            buffer[size++] = ' ';

            open_files++;

            return SUCCEEDED;
        }

        tmp_b = malloc(sizeof(char) * (size + file_size + 4));
        if (tmp_b == NULL) {
            sprintf(emsg, "Out of memory while trying to expand the project to incorporate file \"%s\".\n", full_name);
            print_error(emsg, ERROR_INC);
            return FAILED;
        }

        /* reallocate tmp_a */
        if (tmp_a_size < file_size + 4) {
            if (tmp_a != NULL)
                free(tmp_a);

            tmp_a = malloc(sizeof(char) * (file_size + 4));
            if (tmp_a == NULL) {
                sprintf(emsg, "Out of memory while allocating new room for \"%s\".\n", full_name);
                print_error(emsg, ERROR_INC);
                return FAILED;
            }

            tmp_a_size = file_size + 4;
        }

        /* preprocess */
        inz = 0;
        preprocess_file(include_in_tmp, include_in_tmp + file_size, tmp_a, &inz, full_name);

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

        return SUCCEEDED;
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
        tmp_c = tmp_c == null?("." + File.pathSeparator):tmp_c;
        name = name == null?"":name;
        return tmp_c + name;
    }

    private static void generate_extra_definitions() {
        String dateString;

        /* generate WLA_TIME */
        dateString = new Date().toString();

        flags.add_a_new_definition("WLA_TIME", 0.0, dateString, DEFINITION_TYPE_STRING);
        flags.add_a_new_definition("wla_time", 0.0, dateString, DEFINITION_TYPE_STRING);
        flags.add_a_new_definition("WLA_VERSION", 0.0, wla_version, DEFINITION_TYPE_STRING);
        flags.add_a_new_definition("wla_version", 0.0, wla_version, DEFINITION_TYPE_STRING);



    }


    private static void printDefaultMessage() {
        System.out.println("65816 Macro Assembler for Java based on ");
        System.out.println("WLA 65816 Macro Assembler v9.8a");
        System.out.println("Java port by secondsun https://github.com/secondsun/snes-dev-tools");
        System.out.println("Based on codoe written by Ville Helin in 1998-2008 - In GitHub since 2014: https://github.com/vhelin/wla-dx\n");
        System.out.println(("USAGE: Main [OPTIONS] <ASM FILE>"));
        System.out.println("Options:");
        System.out.println("-i  Add list file information");
        System.out.println("-M  Output makefile rules");
        System.out.println("-q  Quiet");
        System.out.println("-t  Test compile");
        System.out.println("-v  Verbose messages");
        System.out.println("-x  Extra compile time definitions");
        System.out.println("-I [DIR]  Include directory");
        System.out.println("-D [DEF]  Declare definition");
        System.out.println("Output types:");
        System.out.println("-o [FILE]  Output object file");
        System.out.println("-l [FILE]  Output library file");
    }

    private static void proceduresAtExit() {
        if (gba_tmp_file != null) {
            gba_tmp_file.deleteOnExit();
        }
    }


}
