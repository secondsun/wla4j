/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sagaoftherealms.tools.snes.assembler.pass;

import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.macro.MacroRuntime;
import net.sagaoftherealms.tools.snes.assembler.pass.macro.MacroStatic;

import java.io.OutputStream;
import java.io.PrintStream;

import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_LIBRARY;

/**
 * @author summers
 */
public class Pass1 {

    private final InputData data;
    private final Flags flags;
    Slot slots[] = new Slot[256];
    private boolean newline_beginning;
    private boolean line_count_status;
    private boolean listfile_data;
    private OutputStream output = System.out;


    public Pass1(InputData data, Flags flags) {
        this.data = data;
        this.flags = flags;
    }

    public void pass() {
        MacroStatic m;
        MacroRuntime mrt;


        int slotIndex = 0;

        for (slotIndex = 0; slotIndex < 256; slotIndex++) {
            slots[slotIndex] = new Slot(0, 0);
        }

        //data.buffer.rewind();


        /* output the file id */
        //((PrintStream) output).print(String.format("f%d ", data.getActiveFile().filename_id));


        /* BANK 0 SLOT 0 ORG 0 */
        if (flags.getOutputFormat() != OUTPUT_LIBRARY) {
            ((PrintStream) output).print(String.format("B%d %d O%d", 0, 0, 0));
        }
//
//        while ((t = get_next_token()) == SUCCEEDED) {
//            q = evaluate_token();
//
//            if (q == SUCCEEDED)
//                continue;
//            else if (q == EVALUATE_TOKEN_EOP) {
//                /*write_log_summers(buffer, size);*/
//                return SUCCEEDED;
//            }
            /*
            else if (q == EVALUATE_TOKEN_NOT_IDENTIFIED) {
                /* check if it is of the form "LABEL:XYZ" * /
                for (q = 0; q < ss; q++)
                    if (tmp[q] == ':')
                        break;

                /* is it a macro? * /
                if (q == ss)
                    m = macro_get(tmp);

                /* it is a label after all? * /
                if (q != ss || newline_beginning == ON) {
                    tmp[q] = 0;

                    /* reset the flag as there can be only one label / line * /
                    newline_beginning = OFF;

                    if (output_format == OUTPUT_LIBRARY && section_status == OFF) {
                        print_error("All labels must be inside sections when compiling a library.\n", ERROR_LOG);
                        return FAILED;
                    }
                    if (org_defined == 0) {
                        sprintf(emsg, "\"%s\" needs a position in memory.\n", tmp);
                        print_error(emsg, ERROR_LOG);
                        return FAILED;
                    }
                    if (ss >= MAX_NAME_LENGTH) {
                        sprintf(emsg, "The label \"%s\" is too long. Max label length is %d characters.\n", tmp, MAX_NAME_LENGTH);
                        print_error(emsg, ERROR_NONE);
                        return FAILED;
                    }
                    if (bankheader_status == ON) {
                        print_error("BANKHEADER sections don't take labels.\n", ERROR_LOG);
                        return FAILED;
                    }

                    /* check out for \@-symbols * /
                    if (macro_active != 0) {
                        if (tmp[q - 2] == '\\' && tmp[q - 1] == '@')
                            sprintf(&tmp[q - 2], "%d%c", macro_runtime_current->macro->calls - 1, 0);
                    }

                    fprintf(file_out_ptr, "k%d L%s ", active_file_info_last->line_current, tmp);

                    /* move to the end of the label * /
                    if (q != ss)
                        i -= ss - q - 1;
                    else
                        i -= ss - q;

                    continue;
                }

                if (m == NULL) {
                    sprintf(emsg, "Unknown symbol \"%s\".\n", tmp);
                    print_error(emsg, ERROR_ERR);
                    return FAILED;
                }

                /* start running a macro... run until .ENDM * /
                if (macro_stack_grow() == FAILED)
                    return FAILED;

                mrt = &macro_stack[macro_active];
                mrt->argument_data = NULL;

                /* collect macro arguments * /
                for (p = 0; 1; p++) {
                    /* take away the white space * /
                    while (1) {
                        if (buffer[i] == ' ' || buffer[i] == ',')
                            i++;
                        else
                            break;
                    }

                    o = i;
                    q = input_number();
                    if (q == INPUT_NUMBER_EOL)
                        break;

                    mrt->argument_data = realloc(mrt->argument_data, (p+1)*sizeof(struct macro_argument *));
                    mrt->argument_data[p] = malloc(sizeof(struct macro_argument));
                    if (mrt->argument_data == NULL || mrt->argument_data[p] == NULL) {
                        print_error("Out of memory error while collecting macro arguments.\n", ERROR_NONE);
                        return FAILED;
                    }

                    mrt->argument_data[p]->start = o;
                    mrt->argument_data[p]->type = q;

                    if (q == INPUT_NUMBER_ADDRESS_LABEL)
                        strcpy(mrt->argument_data[p]->string, label);
        else if (q == INPUT_NUMBER_STRING)
                        strcpy(mrt->argument_data[p]->string, label);
        else if (q == INPUT_NUMBER_STACK)
                        mrt->argument_data[p]->value = latest_stack;
        else if (q == SUCCEEDED)
                        mrt->argument_data[p]->value = d;
        else
                    return FAILED;

                    /* do we have a name for this argument? * /
                    if (p < m->nargument_names) {
                        if (q == INPUT_NUMBER_ADDRESS_LABEL)
                            redefine(m->argument_names[p], 0.0, label, DEFINITION_TYPE_ADDRESS_LABEL, strlen(label));
                        else if (q == INPUT_NUMBER_STRING)
                            redefine(m->argument_names[p], 0.0, label, DEFINITION_TYPE_STRING, strlen(label));
                        else if (q == INPUT_NUMBER_STACK)
                            redefine(m->argument_names[p], (double)latest_stack, NULL, DEFINITION_TYPE_STACK, 0);
                        else if (q == SUCCEEDED)
                            redefine(m->argument_names[p], (double)d, NULL, DEFINITION_TYPE_VALUE, 0);
                    }
                }

                next_line();

                mrt->supplied_arguments = p;
                if (macro_start(m, mrt, MACRO_CALLER_NORMAL, p) == FAILED)
                    return FAILED;

                continue;
            }
            else if (q == FAILED) {
                sprintf(emsg, "Couldn't parse \"%s\".\n", tmp);
                print_error(emsg, ERROR_ERR);
                return FAILED;
            }
            else {
                printf("PASS_1: Internal error, unknown return type %d.\n", q);
                return FAILED;
            }
        }

        return FAILED;
        * 
        * */
        //}
    }

    String get_next_token() {
return null;
//        StringBuilder nextToken = new StringBuilder();
//
//        char nextChar = data.buffer.get();
//
//        while (true) {
//            if (data.buffer.length() == data.buffer.position()) {
//                break;
//            } else if (nextChar == ' ') {
//                newline_beginning = false;
//                continue;
//            }
//            if (nextChar == 0xA) {
//                next_line();
//                continue;
//            }
//            break;
//        }
//
//        if (buffer[i] == '"') {
//            for (ss = 0, i++; buffer[i] != 0xA && buffer[i] != '"'; ) {
//                if (buffer[i] == '\\' && buffer[i + 1] == '"') {
//                    tmp[ss++] = '"';
//                    i += 2;
//                } else
//                    tmp[ss++] = buffer[i++];
//            }
//
//            if (buffer[i] == 0xA) {
//                print_error("GET_NEXT_TOKEN: String wasn't terminated properly.\n", ERROR_NONE);
//                return FAILED;
//            }
//            tmp[ss] = 0;
//            i++;
//
//            /* expand e.g., \1 and \@ */
//            if (macro_active != 0) {
//                if (expand_macro_arguments(tmp) == FAILED)
//                    return FAILED;
//                ss = strlen(tmp);
//            }
//
//            return GET_NEXT_TOKEN_STRING;
//        }
//
//        if (buffer[i] == '.') {
//            tmp[0] = '.';
//            i++;
//            for (ss = 1; buffer[i] != 0x0A && buffer[i] != ' ' && ss < MAX_NAME_LENGTH; ) {
//                tmp[ss] = buffer[i];
//                cp[ss - 1] = toupper((int) buffer[i]);
//                i++;
//                ss++;
//            }
//            cp[ss - 1] = 0;
//        } else if (buffer[i] == '=' || buffer[i] == '>' || buffer[i] == '<' || buffer[i] == '!') {
//            for (ss = 0; buffer[i] != 0xA && (buffer[i] == '=' || buffer[i] == '!' || buffer[i] == '<' || buffer[i] == '>')
//                    && ss < MAX_NAME_LENGTH; tmp[ss++] = buffer[i++])
//                ;
//        } else {
//            for (ss = 0; buffer[i] != 0xA && buffer[i] != ',' && buffer[i] != ' ' && ss < MAX_NAME_LENGTH; ) {
//                tmp[ss] = buffer[i];
//                ss++;
//                i++;
//            }
//            if (buffer[i] == ',')
//                i++;
//        }
//
//        if (ss >= MAX_NAME_LENGTH) {
//            print_error("GET_NEXT_TOKEN: Too long for a token.\n", ERROR_NONE);
//            return FAILED;
//        }
//
//        tmp[ss] = 0;

        /* expand e.g., \1 and \@ */
//        if (macro_active != 0) {
//            if (expand_macro_arguments(tmp) == FAILED)
//                return FAILED;
//            ss = strlen(tmp);
//        }
//
//        return SUCCEEDED;
    }

    private void next_line() {
        newline_beginning = true;

        if (line_count_status == false) {
            return;
        }

        /* output the file number for list file structure building */
//        if (listfile_data == true) {
//            ((PrintStream) output).print(String.format("k%d ", active_file_info_last.));
//        }
//
//        if (active_file_info_last != NULL) {
//            active_file_info_last -> line_current++;
//        }
    }


}
