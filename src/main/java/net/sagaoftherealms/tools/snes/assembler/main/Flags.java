package net.sagaoftherealms.tools.snes.assembler.main;

import net.sagaoftherealms.tools.snes.assembler.Defines;
import net.sagaoftherealms.tools.snes.assembler.Definition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;
import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_VALUE;
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
    private Map<String, Definition> definitions = new HashMap<>();
    private Defines.Output output_format = OUTPUT_NONE;
    private String final_name;
    private String ext_incdir;
    private boolean use_incdir =  false;
    private boolean listfile_data =  false;
    private boolean verbose_mode = false;
    private boolean test_mode = false;
    private boolean makefile_rules = false;
    private boolean quiet = false;
    private boolean extra_definitions = false;
    private String asm_name;

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
                        throw new RuntimeException(output_format + " != OUTPUT_NONE");
                    output_format = OUTPUT_LIBRARY;
                    if (count + 1 < flagc) {
                        /* set output */
                        final_name = flags[count + 1];
                    } else {
                        throw new RuntimeException("parse error");
                    }
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
                    } else {
                        throw new RuntimeException("parse error");
                    }

                    count++;
                    break;
                }

                case "-I": {
                    if (count + 1 < flagc) {
                        /* get arg */
                        parse_and_set_incdir(flags[count + 1], NO);
                    } else {
                        throw new RuntimeException("Unknown Flag " + flags[count] +  ":" + flags[count + 1]);
                    }
                    count++;
                    break;
                }
                case "-i": {
                    listfile_data = true;
                    break;
                }
                case "-v": {
                    verbose_mode = true;
                    break;
                }
                case "-t": {
                    test_mode = true;
                    break;
                }
                case "-M": {
                    makefile_rules = true;
                    test_mode = true;
                    verbose_mode = false;
                    quiet = true;
                    break;
                }
                case "-q": {
                    quiet = true;
                    break;
                }
                case "-x": {
                    extra_definitions = true;
                    break;
                }
                default: {
                    if (count == flags.length-1) {
                        asm_name = (flags[count]);
                        count++;
                        asm_name_def++;
                    } else {
                        /* legacy support? */
                        if (flags[count].equals("-D")) {
                            /* old define */
                            parse_and_add_definition(flags[count], YES);
                        } else if (flags[count].equals("-I")) {
                            /* old include directory */
                            parse_and_set_incdir(flags[count], YES);
                            continue;
                        } else
                            throw new RuntimeException("Unknown Flag " + flags[count]);
                    }
                    break;
                }


            }

        }

        if (asm_name_def <= 0)
            throw new RuntimeException("No assembly name definition passed.");


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


            ext_incdir = name + File.separatorChar;
            use_incdir = true;

            return Result.SUCCEEDED;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    void parse_and_add_definition(String flag, Defines.YesNo contains_flag) {

        String n;
        String flagsAfterSplit[];

        int i;

        /* skip the flag? */
        if (contains_flag == YES)
            flag = flag.substring(2);

        flagsAfterSplit = flag.split("=");
        for (int index = 0; index < flagsAfterSplit.length; index++) {
            String key = flagsAfterSplit[index];
            String value = null;
            ++index;
            int intValue = -1;

            if (index < flagsAfterSplit.length) {
                value = flagsAfterSplit[index];
                if (value.startsWith("$") || ((value.endsWith("h") || value.endsWith("H")) && ((value.matches("[0-9a-fA-F]+[hH]$"))))) {

                    try {
                        value = value.replace("$", "").replace("h", "").replace("H", "");
                        intValue = Integer.parseInt(value, 16);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    add_a_new_definition(key, intValue, null, DEFINITION_TYPE_VALUE);
                } else if (value.matches("[0-9]+")) {
                    try {
                        intValue = Integer.parseInt(value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    add_a_new_definition(key, intValue, null, DEFINITION_TYPE_VALUE);
                } else {
                    add_a_new_definition(key, 0, value, DEFINITION_TYPE_STRING);
                }
            } else {
                add_a_new_definition(key, 0.0, null, DEFINITION_TYPE_VALUE);
            }

        }

    }

    private void add_a_new_definition(String key, double doubleValue, String stringVal, Defines.DefinitionType type) {
        var definition = definitions.get(key);
        if (definition != null) {
            throw new RuntimeException(key + " already defined");
        }

        switch (type) {

            case DEFINITION_TYPE_VALUE:
            case DEFINITION_TYPE_STACK:
                definition = new Definition(doubleValue, type);
                break;
            case DEFINITION_TYPE_STRING:
            case DEFINITION_TYPE_ADDRESS_LABEL:
                definition = new Definition(stringVal, type);
                break;
        }

        definitions.put(key, definition);

    }


}
