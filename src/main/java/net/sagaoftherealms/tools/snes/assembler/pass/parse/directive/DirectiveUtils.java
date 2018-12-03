package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

public final class DirectiveUtils {

  private DirectiveUtils() {}

  public static DirectiveParser getParser(AllDirectives type) {
    switch (type) {
      case ENUM:
        return new EnumParser();
      case STRUCT:
        return new StructParser();
      case IF:

      case IFDEF:

      case IFEXISTS:

      case IFNDEF:

      case IFDEFM:

      case IFNDEFM:

      case IFEQ:

      case IFNEQ:

      case IFLE:

      case IFLEEQ:

      case IFGR:

      case IFGREQ:
        return new IfParser(type);
      default:
        return new GenericDirectiveParser(type);
    }
  }

  public static DirectiveNode createDirectiveNode(String directiveName) {
    AllDirectives directive = AllDirectives.valueOf(directiveName.replace(".", "").toUpperCase());
    DirectiveNode node;
    switch (directive) {
      case ENUM:
        node = new EnumNode();
        break;

      case STRUCT:
        node = new StructNode();
        break;
      case IF:

      case IFDEF:

      case IFEXISTS:

      case IFNDEF:

      case IFDEFM:

      case IFNDEFM:

      case IFEQ:

      case IFNEQ:

      case IFLE:

      case IFLEEQ:

      case IFGR:

      case IFGREQ:

      case ELSE:

      case ENDIF:


      case UNDEF:

      case EIGHT_BIT:

      case SIXTEEN_BIT:

      case TWENTYFOUR_BIT:

      case ACCU:

      case INDEX:

      case ASM:

      case ENDASM:

      case DBRND:

      case DWRND:

      case DBCOS:

      case DBSIN:

      case DWCOS:

      case DWSIN:

      case ROMBANKS:

      case EMPTYFILL:

      case COMPUTESNESCHECKSUM:

      case INCDIR:

      case INCLUDE:

      case INCBIN:

      case INPUT:

      case BACKGROUND:

      case UNBACKGROUND:

      case FAIL:

      case FCLOSE:

      case FOPEN:

      case FREAD:

      case FSIZE:

      case MACRO:

      case ENDM:

      case SHIFT:

      case FASTROM:

      case SLOWROM:

      case SMC:

      case HIROM:

      case EXHIROM:

      case LOROM:

      case BASE:

      case BLOCK:

      case ENDB:

      case BANK:

      case SLOT:

      case ROMBANKSIZE:

      case ORG:

      case ORGA:

      case DS:

      case DSB:

      case DSTRUCT:

      case DSW:

      case DB:

      case BYT:

      case DBM:

      case SYM:

      case SYMBOL:

      case BR:

      case BREAKPOINT:

      case ASCIITABLE:

      case ENDA:

      case ASCTABLE:

      case ASC:

      case DW:

      case WORD:

      case DWM:

      case DEFINE:

      case DEF:

      case EQU:

      case REDEFINE:

      case REDEF:
      case UNDEFINE:

      case REPEAT:

      case REPT:

      case ENDR:

      case ENDE:

      case ENDST:

      case MEMORYMAP:

      case ENDME:

      case ROMBANKMAP:

      case ENDRO:

      case SEED:

      case SECTION_BANKSECTION:

      case SECTION:

      case RAMSECTION:

      case ENDS:

      case EXPORT:

      case PRINTT:

      case PRINTV:

      case OUTNAME:

      case SNESHEADER:

      case ENDSNES:

      case SNESNATIVEVECTOR:

      case ENDNATIVEVECTOR:

      case SNESEMUVECTOR:

      case ENDEMUVECTOR:

      default:
        node = new DirectiveNode(directive);
    }

    return node;
  }

  public static NodeTypes getDirectiveNodeType(AllDirectives directive) {
    return directive == AllDirectives.ENUM ? NodeTypes.ENUM : NodeTypes.DIRECTIVE;
  }
}
