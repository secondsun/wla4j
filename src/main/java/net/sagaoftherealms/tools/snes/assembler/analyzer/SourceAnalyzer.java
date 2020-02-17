package net.sagaoftherealms.tools.snes.assembler.analyzer;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

import java.util.ArrayList;
import java.util.List;

public class SourceAnalyzer {
    private final Context context;


    public SourceAnalyzer(Context context) {
        this.context = context;
    }

    public SourceAnalyzer() {
        this(new Context());
    }

    /**
     *
     * This will scan an entire project, collect errors, etc.
     * This will update all of the internal state for the file and any files included in it.
     *
     * @param fileName the filename for the file the main list represents
     * @param main the nodes parse from fileName
     * @return a list of errors, never null
     */
    public List<ErrorNode> analyzeProject(String fileName, List<Node> main) {

        List<ErrorNode> toReturn = new ArrayList<>();

        for (Node node : main) {
            switch (node.getType()) {

                case DIRECTIVE_ARGUMENTS -> {
                }
                case DIRECTIVE_BODY -> {
                }
                case DIRECTIVE -> {
                    toReturn.addAll(analyizeDirective((DirectiveNode)node));
                }
                case SECTION -> {
                }
                case LABEL -> {
                }
                case OPCODE -> {
                }
                case OPCODE_ARGUMENT -> {
                }
                case NUMERIC_EXPRESION -> {
                }
                case NUMERIC_CONSTANT -> {
                }
                case MACRO -> {
                }
                case STRING_EXPRESSION -> {
                }
                case IDENTIFIER_EXPRESSION -> {
                }
                case LABEL_DEFINITION -> {
                }
                case MACRO_CALL -> {
                }
                case SLOT -> {
                }
                case MACRO_BODY -> {
                }
                case ERROR -> {
                }
                case ENUM -> {
                }
            }
        }

        return toReturn;
    }

    private List<? extends ErrorNode> analyizeDirective(DirectiveNode node) {
        List<ErrorNode> errors = new ArrayList<>();
        switch (node.getDirectiveType()) {

            case EIGHT_BIT -> {
            }
            case SIXTEEN_BIT -> {
            }
            case TWENTYFOUR_BIT -> {
            }
            case ACCU -> {
            }
            case INDEX -> {
            }
            case ASM -> {
            }
            case ENDASM -> {
            }
            case DBRND -> {
            }
            case DWRND -> {
            }
            case DBCOS -> {
            }
            case DBSIN -> {
            }
            case DWCOS -> {
            }
            case DWSIN -> {
            }
            case ROMBANKS -> {
                RomBanksAnalyzer bsa = new RomBanksAnalyzer(this.context);
                errors.addAll(bsa.checkDirective(node));
            }
            case EMPTYFILL -> {
            }
            case COMPUTESNESCHECKSUM -> {
            }
            case INCDIR -> {
            }
            case INCLUDE -> {
            }
            case INCBIN -> {
            }
            case INPUT -> {
            }
            case BACKGROUND -> {
            }
            case UNBACKGROUND -> {
            }
            case FAIL -> {
            }
            case FCLOSE -> {
            }
            case FOPEN -> {
            }
            case FREAD -> {
            }
            case FSIZE -> {
            }
            case MACRO -> {
            }
            case ENDM -> {
            }
            case SHIFT -> {
            }
            case FASTROM -> {
            }
            case SLOWROM -> {
            }
            case SMC -> {
            }
            case HIROM -> {
            }
            case EXHIROM -> {
            }
            case LOROM -> {
            }
            case BASE -> {
            }
            case BLOCK -> {
            }
            case ENDB -> {
            }
            case BANK -> {
            }
            case SLOT -> {
            }
            case ROMBANKSIZE, BANKSIZE -> {
                BankSizeAnalyzer bsa = new BankSizeAnalyzer(this.context);
                errors.addAll(bsa.checkDirective(node));
            }
            case ORG -> {
            }
            case ORGA -> {
            }
            case DS -> {
            }
            case DSB -> {
            }
            case DSTRUCT -> {
            }
            case DSW -> {
            }
            case DB -> {
            }
            case BYTE -> {
            }
            case BYT -> {
            }
            case DBM -> {
            }
            case SYM -> {
            }
            case SYMBOL -> {
            }
            case BR -> {
            }
            case BREAKPOINT -> {
            }
            case ASCIITABLE -> {
            }
            case ENDA -> {
            }
            case ASCTABLE -> {
            }
            case ASC -> {
            }
            case DW -> {
            }
            case WORD -> {
            }
            case DWM -> {
            }
            case DEFINE -> {
            }
            case DEF -> {
            }
            case EQU -> {
            }
            case REDEFINE -> {
            }
            case REDEF -> {
            }
            case IF -> {
            }
            case IFDEF -> {
            }
            case IFEXISTS -> {
            }
            case UNDEFINE -> {
            }
            case UNDEF -> {
            }
            case IFNDEF -> {
            }
            case IFDEFM -> {
            }
            case IFNDEFM -> {
            }
            case IFEQ -> {
            }
            case IFNEQ -> {
            }
            case IFLE -> {
            }
            case IFLEEQ -> {
            }
            case IFGR -> {
            }
            case IFGREQ -> {
            }
            case ELSE -> {
            }
            case ENDIF -> {
            }
            case REPEAT -> {
            }
            case REPT -> {
            }
            case ENDR -> {
            }
            case ENUM -> {
            }
            case ENDE -> {
            }
            case STRUCT -> {
            }
            case ENDST -> {
            }
            case MEMORYMAP -> {
                MemoryMapAnalyzer mapAnal = new MemoryMapAnalyzer(this.context);
                errors.addAll(mapAnal.checkDirective(node));
            }
            case ENDME -> {
            }
            case ROMBANKMAP -> {
            }
            case ENDRO -> {
            }
            case SEED -> {
            }
            case SECTION_BANKSECTION -> {
            }
            case SECTION -> {
            }
            case RAMSECTION -> {
            }
            case ENDS -> {
            }
            case ENDGB -> {
            }
            case EXPORT -> {
            }
            case PRINTT -> {
            }
            case PRINTV -> {
            }
            case OUTNAME -> {
            }
            case SNESHEADER -> {
            }
            case ENDSNES -> {
            }
            case SNESNATIVEVECTOR -> {
            }
            case ENDNATIVEVECTOR -> {
            }
            case SNESEMUVECTOR -> {
            }
            case ENDEMUVECTOR -> {
            }
            case COMPUTEGBCHECKSUM -> {
            }
            case CARTRIDGETYPE -> {
            }
            case COUNTRYCODE -> {
            }
            case VERSION -> {
            }
            case DESTINATIONCODE -> {
            }
            case NINTENDOLOGO -> {
            }
            case GBHEADER -> {
            }
            case SMSHEADER -> {
            }
            case COMPUTEGBCOMPLEMENTCHECK -> {
            }
            case LICENSEECODENEW -> {
            }
            case LICENSEECODEOLD -> {
            }
            case NAME -> {
            }
            case RAMSIZE -> {
            }
            case ROMDMG -> {
            }
            case ROMGBC -> {
            }
            case ROMGBCONLY -> {
            }
            case ROMSGB -> {
            }
            case UNION -> {
            }
            case NEXTU -> {
            }
            case ENDU -> {
            }
        }
        return errors;
    }


}
