package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfDefForMacrosParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineByteParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineByteSeriesParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineWordParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineWordSeriesParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamSectionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public final class DirectiveUtils {

  private DirectiveUtils() {}

  public static DirectiveParser getParser(AllDirectives type) {
    switch (type) {
      case ENUM:
        return new EnumParser();
      case STRUCT:
        return new StructParser();
      case IFDEFM:
      case IFNDEFM:
        return new IfDefForMacrosParser(type);
      case IF:

      case IFDEF:

      case IFEXISTS:

      case IFNDEF:

      case IFEQ:

      case IFNEQ:

      case IFLE:

      case IFLEEQ:

      case IFGR:

      case IFGREQ:
        return new IfParser(type);
      case SECTION:
        return new SectionParser();
      case RAMSECTION:
        return new RamSectionParser();
      case MACRO:
        return new MacroParser();
      case DB:
      case BYT:
      case BYTE:
        return new DefineByteParser(type);
      case DW:
      case WORD:
        return new DefineWordParser(type);
      case DS:
      case DSB:
        return new DefineByteSeriesParser(type);
      case DSW:
        return new DefineWordSeriesParser(type);
      default:
        return new GenericDirectiveParser(type);
    }
  }

  /**
   * Creates the appropriate class of directive node
   * 
   * @param directiveName the name of the directive
   * @param token the token that the directive begins at
   * @return a directive node with all of its arguments and body
   */
  public static DirectiveNode createDirectiveNode(String directiveName, Token token) {
    AllDirectives directive = AllDirectives.valueOf(directiveName.replace(".", "").toUpperCase());
    DirectiveNode node;
    switch (directive) {
      case ENUM:
        node = new EnumNode();
        break;

      case STRUCT:
        node = new StructNode();
        break;
      case SECTION:
        node = new SectionNode();
        break;
      case MACRO:
        node = new MacroNode(token);
        break;
      case IFDEFM:
      case IFNDEFM:
      case IF:

      case IFDEF:

      case IFEXISTS:

      case IFNDEF:

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
      case BYTE:

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
