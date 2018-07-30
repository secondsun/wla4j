package net.sagaoftherealms.tools.snes.assembler.main;

import net.sagaoftherealms.tools.snes.assembler.Defines;

import java.io.File;

import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_LIBRARY;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_NONE;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_OBJECT;
import static net.sagaoftherealms.tools.snes.assembler.Defines.YesNo.NO;
import static net.sagaoftherealms.tools.snes.assembler.Defines.YesNo.YES;

/**
 * This class parses and represents flags.
 */
public class Flags {

    private static final int MAX_NAME_LENGTH = 255;
    private final Result result;
    private final String[] flags;

    private Defines.Output output_format = OUTPUT_NONE;
    private String final_name;
    private String ext_incdir;
    private Defines.YesNo use_incdir = NO;

    public enum Result {FAILED, SUCCEEDED}

    public Flags(String... flags) {
        this.flags = flags;
        this.result = parse();
    }

    public Result parse() {
        int count;
        int asm_name_def = 0;
        StringBuilder str_build = null;
        int flagc = flags.length;

        for (count = 1; count < flags.length; count++) {

            switch (flags[count]) {
                case "-o": {
                    if (output_format != OUTPUT_NONE)
                        return Result.FAILED;
                    output_format = OUTPUT_OBJECT;
                    if (count + 1 < flagc) {
                        /* set output */
                        final_name = flags[count + 1];
                    } else
                        return Result.FAILED;

                    count++;
                    break;
                }

                case "-l": {
                    if (output_format != OUTPUT_NONE)
                        return Result.FAILED;
                    output_format = OUTPUT_LIBRARY;
                    if (count + 1 < flagc) {
                        /* set output */
                        final_name = flags[count + 1];
                    } else
                        return Result.FAILED;

                    count++;
                    break;
                }

                case "-D": {
                    if (count + 1 < flagc) {
                        if (count + 3 < flagc) {
                            if (!(flags[count + 2].equals("="))) {
                                str_build = new StringBuilder((flags[count + 1].length()) + (flags[count + 3]).length() + 2);
                                str_build.append(String.format("%s=%s", flags[count + 1], flags[count + 3]));
                                parse_and_add_definition(str_build.toString(), NO);
                                str_build = null;
                                count += 2;
                            } else
                                parse_and_add_definition(flags[count + 1], NO);
                        } else
                            parse_and_add_definition(flags[count + 1], NO);
                    } else
                        return Result.FAILED;

                    count++;
                    break;
                }

                case "-I": {
                    if (count + 1 < flagc) {
                        /* get arg */
                        parse_and_set_incdir(flags[count + 1], NO);
                    } else
                        return Result.FAILED;

                    count++;
                    break;
                }
                default: {
                    break;
                }


            }


             else if (!strcmp(flags[count], "-I")) {
                if (count + 1 < flagc) {
                    /* get arg */
                    parse_and_set_incdir(flags[count + 1], NO);
                } else
                    return Result.FAILED;

                count++;
                continue;
            } else if (!strcmp(flags[count], "-i")) {
                listfile_data = YES;
                continue;
            } else if (!strcmp(flags[count], "-v")) {
                verbose_mode = ON;
                continue;
            } else if (!strcmp(flags[count], "-t")) {
                test_mode = ON;
                continue;
            } else if (!strcmp(flags[count], "-M")) {
                makefile_rules = YES;
                test_mode = ON;
                verbose_mode = OFF;
                quiet = YES;
                continue;
            } else if (!strcmp(flags[count], "-q")) {
                quiet = YES;
                continue;
            } else if (!strcmp(flags[count], "-x")) {
                extra_definitions = ON;
                continue;
            } else {
                if (count == flagc - 1) {
                    asm_name = malloc(strlen(flags[count]) + 1);
                    strcpy(asm_name, flags[count]);
                    count++;
                    asm_name_def++;
                } else {
                    /* legacy support? */
                    if (strncmp(flags[count], "-D", 2) == 0) {
                        /* old define */
                        parse_and_add_definition(flags[count], YES);
                        continue;
                    } else if (strncmp(flags[count], "-I", 2) == 0) {
                        /* old include directory */
                        parse_and_set_incdir(flags[count], YES);
                        continue;
                    } else
                        return Result.FAILED;
                }
            }
        }

        if (asm_name_def <= 0)
            return Result.FAILED;


        return Result.SUCCEEDED;

    }

    private Result parse_and_set_incdir(String flag, Defines.YesNo contains_flag) {
        try {
            String name;
            int i;

            /* skip the flag? */
            if (contains_flag == YES)
                flag = flag.substring(2);

            name = flag;


            localize_path(n);

            ext_incdir = name + File.separatorChar;
            use_incdir = YES;

            return Result.SUCCEEDED;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    int parse_and_add_definition(String c, Defines.YesNo contains_flag) {

        String n;
        int stringIndex = 0;
        int i;

        /* skip the flag? */
        if (contains_flag == YES)
            stringIndex  += 2;

        for (i = 0; i < MAX_NAME_LENGTH && *c != 0 && *c != '=';i++, c++)
        n[i] = *c;
        n[i] = 0;

        if (*c == 0)
        return add_a_new_definition(n, 0.0, NULL, DEFINITION_TYPE_VALUE, 0);
  else if (*c == '='){
            c++;
            if (*c == 0)
            return FAILED;

            /* hexadecimal value? */
            if (*c == '$' || ((c[strlen(c) - 1] == 'h' || c[strlen(c) - 1] == 'H') && ( * c >= '0' && * c <= '9'))){
                if (*c == '$')
                c++;
                for (i = 0; *c != 0;
                c++){
                    if (*c >= '0' && *c <= '9')
                    i = (i << 4) + *c - '0';
	else if (*c >= 'a' && *c <= 'f')
                    i = (i << 4) + *c - 'a' + 10;
	else if (*c >= 'A' && *c <= 'F')
                    i = (i << 4) + *c - 'A' + 10;
	else if (( * c == 'h' || *c == 'H') && *(c + 1) == 0)
                    break;
	else{
                        fprintf(stderr, "PARSE_AND_ADD_DEFINITION: Error in value.\n");
                        return FAILED;
                    }
                }
                return add_a_new_definition(n, (double) i, NULL, DEFINITION_TYPE_VALUE, 0);
            }

            /* decimal value? */
            if (*c >= '0' && *c <= '9'){
                for (i = 0; *c != 0;
                c++){
                    if (*c >= '0' && *c <= '9')
                    i = (i * 10) + *c - '0';
	else{
                        fprintf(stderr, "PARSE_AND_ADD_DEFINITION: Error in value.\n");
                        return FAILED;
                    }
                }
                return add_a_new_definition(n, (double) i, NULL, DEFINITION_TYPE_VALUE, 0);
            }

            /* string definition */
            return add_a_new_definition(n, 0.0, c, DEFINITION_TYPE_STRING, strlen(c));
        }

        return FAILED;
    }

}
