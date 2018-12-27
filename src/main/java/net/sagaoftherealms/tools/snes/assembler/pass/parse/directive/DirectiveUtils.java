package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.COMMA;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.bank.BankNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.bank.BankParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfDefForMacrosParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineByteParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineByteSeriesParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineWordParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefineWordSeriesParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.RepeatParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamSectionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public final class DirectiveUtils {

  private static DirectiveParser orgParser =
      parser -> {
        DirectiveArgumentsNode node = new DirectiveArgumentsNode();
        var expression = ExpressionParser.expressionNode(parser);
        parser.consumeAndClear(EOL, END_OF_INPUT);
        node.add(expression);
        return node;
      };

  private static DirectiveParser redefParser =
      parser -> {
        DirectiveArgumentsNode node = new DirectiveArgumentsNode();

        var token = parser.getCurrentToken();
        parser.consume(LABEL);
        node.add(token.getString());

        token = parser.getCurrentToken();
        while (!(token.getType().equals(EOL) || token.getType().equals(END_OF_INPUT))) {
          if (token.getType().equals(COMMA)) {
            parser.consume(COMMA);
            token = parser.getCurrentToken();
            continue;
          }
          var expression = ExpressionParser.expressionNode(parser);
          node.add(expression);
          token = parser.getCurrentToken();
        }

        parser.consumeAndClear(EOL, END_OF_INPUT);

        return node;
      };

  private DirectiveUtils() {}

  public static DirectiveParser getParser(AllDirectives type) {
    switch (type) {
      case REDEFINE:
      case REDEF:
        return redefParser;
      case BANK:
        return new BankParser();
      case PRINTV:
        return new PrintvParser();
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
      case ASC:
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
      case REPEAT:
      case REPT:
        return new RepeatParser(type);
      case ORG:
      case ORGA:
        return orgParser;
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
    // A few directive enums don't match the source pattern.  We do a manual mapping here.
    switch (directiveName.toUpperCase()) {
      case ".8BIT":
        directiveName = ".EIGHT_BIT";
        break;
      default:
        break;
    }
    AllDirectives directive = AllDirectives.valueOf(directiveName.replace(".", "").toUpperCase());
    DirectiveNode node;
    switch (directive) {
      case BANK:
        node = new BankNode();
        break;
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
