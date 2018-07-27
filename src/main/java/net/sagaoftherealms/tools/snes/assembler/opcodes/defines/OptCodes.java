package net.sagaoftherealms.tools.snes.assembler.opcodes.defines;

import net.sagaoftherealms.tools.snes.assembler.opcodes.OptCode;

import java.util.concurrent.ConcurrentHashMap;

import static net.sagaoftherealms.tools.snes.assembler.opcodes.defines.Opcodes65816.opt_table;

/**
 * Utility class which provides optcode_tables
 */
public class OptCodes {

    private static final ConcurrentHashMap<OptCode[], int[]> optcode_ns = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<OptCode[], int[]> optcode_ps = new ConcurrentHashMap<>();

    public static int[] opcode_n(OptCode[] codes) {
        return optcode_ns.computeIfAbsent(codes, OptCodes::buildN);
    }

    public static int[] opcode_p(OptCode[] codes) {
        return optcode_ps.computeIfAbsent(codes, OptCodes::buildP);
    }

    private static int[] buildN(OptCode[] optCodes) {
        int opcode_n[] = new int [256];

        String max_name = "";
        int max = 0;
        int x, n;
        char a, b, ob;
        /* generate opcode decoding jump tables */
        for (x = 0; x < 256; x++) {
            opcode_n[x] = 0;
        }

        OptCode[] opt_tmp = optCodes;
        a = 'A';
        b = 'a';
        ob = 'a';
        n = 0;
        x = 0;
        while (opt_table[n].getType() != -1) {
            if (opt_table[n].getOp().length() > max) {
                max = opt_table[n].getOp().length();
                max_name = opt_table[n].getOp();
            }
            if (!opt_tmp[n].getOp().startsWith(a + "")) {
                opcode_n[(int)a] = x;
                opcode_n[(int)b] = x;
                a = opt_tmp[n].getOp().charAt(0);
                b = (a + "").toLowerCase().charAt(0);
                if (ob > b) {
                    throw new RuntimeException(String.format("MAIN: Instruction are NOT in alphabetical order (first letter): '%c' -> '%c'.\n", ob, b));
                }
                ob = b;
                x = 1;
                n++;
            }
            else {
                x++;
                n++;
            }
        }
        opcode_n[(int)a] = x;
        opcode_n[(int)b] = x;

        return opcode_n;
    }

    private static int[] buildP(OptCode[] optCodes) {
        int opcode_p[] = new int [256];

        String max_name = "";
        int max = 0;
        int x, n;
        char a, b, ob;
        /* generate opcode decoding jump tables */
        for (x = 0; x < 256; x++) {
            opcode_p[x] = 0;
        }

        OptCode[] opt_tmp = optCodes;
        a = 'A';
        b = 'a';
        ob = 'a';
        n = 0;
        x = 0;
        opcode_p[(int)a] = 0;
        opcode_p[(int)b] = 0;
        while (opt_table[n].getType() != -1) {
            if (opt_table[n].getOp().length() > max) {
                max = opt_table[n].getOp().length();
                max_name = opt_table[n].getOp();
            }
            if (!opt_tmp[n].getOp().startsWith(a + "")) {
                a = opt_tmp[n].getOp().charAt(0);
                b = (a + "").toLowerCase().charAt(0);
                if (ob > b) {
                    throw new RuntimeException(String.format("MAIN: Instruction are NOT in alphabetical order (first letter): '%c' -> '%c'.\n", ob, b));
                }
                ob = b;
                opcode_p[(int)a] = n;
                opcode_p[(int)b] = n;
                x = 1;
                n++;
            }
            else {
                x++;
                n++;
            }
        }

        return opcode_p;
    }

}
