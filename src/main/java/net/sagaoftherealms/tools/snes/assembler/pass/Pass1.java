/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sagaoftherealms.tools.snes.assembler.pass;

import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.macro.MacroRuntime;
import net.sagaoftherealms.tools.snes.assembler.pass.macro.MacroStatic;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author summers
 */
public class Pass1 {

    private final InputData data;
    Slot slots[] = new Slot[256];
    
    
    public Pass1(InputData data) {
        this.data = data;
    }
    
    public void pass(OutputStream output) {
        MacroStatic m;
        MacroRuntime mrt;
        
        for (int i = 0; i< 256; i++) {
            slots[i] = new Slot(0,0);
        }
        
        data.buffer.rewind();
        /*
        * /* output the file id * /
        fprintf(file_out_ptr, "f%d ", active_file_info_tmp->filename_id);

        /* BANK 0 SLOT 0 ORG 0 * /
        if (output_format != OUTPUT_LIBRARY)
            fprintf(file_out_ptr, "B%d %d O%d", 0, 0, 0);

        while ((t = get_next_token()) == SUCCEEDED) {
            q = evaluate_token();

            if (q == SUCCEEDED)
                continue;
            else if (q == EVALUATE_TOKEN_EOP) {
                /*write_log_summers(buffer, size);* /
                return SUCCEEDED;
            }
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
    }
    
}
