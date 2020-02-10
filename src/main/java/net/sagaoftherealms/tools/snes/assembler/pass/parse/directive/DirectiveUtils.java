package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.*;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.bank.BankNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.bank.BankParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfDefForMacrosParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.gbheader.GBHeaderParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.incbin.IncbinParser;
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
        DirectiveArgumentsNode node = new DirectiveArgumentsNode(parser.getCurrentToken());
        var expression = ExpressionParser.expressionNode(parser);
        parser.consumeAndClear(EOL, END_OF_INPUT);
        node.add(expression);
        return node;
      };

  private static DirectiveParser redefParser =
      parser -> {
        DirectiveArgumentsNode node = new DirectiveArgumentsNode(parser.getCurrentToken());

        var token = parser.getCurrentToken();
        parser.consume(LABEL);
        node.add(new StringExpressionNode(token.getString(), token));

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

  private static DirectiveParser trigDefinesParser =
      parser -> {
        DirectiveArgumentsNode arguments = new DirectiveArgumentsNode(parser.getCurrentToken());
        arguments.add(ExpressionParser.expressionNode(parser));
        if (parser.getCurrentToken().getType().equals(COMMA)) {
          parser.consume(COMMA);
        }
        arguments.add(ExpressionParser.expressionNode(parser));
        if (parser.getCurrentToken().getType().equals(COMMA)) {
          parser.consume(COMMA);
        }
        arguments.add(ExpressionParser.expressionNode(parser));
        if (parser.getCurrentToken().getType().equals(COMMA)) {
          parser.consume(COMMA);
        }
        arguments.add(ExpressionParser.expressionNode(parser));
        if (parser.getCurrentToken().getType().equals(COMMA)) {
          parser.consume(COMMA);
        }
        arguments.add(ExpressionParser.expressionNode(parser));
        return arguments;
      };

  private DirectiveUtils() {}

  public static DirectiveParser getParser(AllDirectives type) {
    switch (type) {
      case MEMORYMAP:
        return new MemoryMapParser();
      case ROMBANKMAP:
        return new RomBankMapParser();
      case GBHEADER:
        return new GBHeaderParser();
      case INCBIN:
        return new IncbinParser();
      case REDEFINE:
      case REDEF:
      case DEF:
      case DEFINE:
        return redefParser;
      case BANK:
        return new BankParser();
      case PRINTV:
        return new PrintvParser();
      case ENUM:
        return new EnumParser();
      case STRUCT:
        return new StructParser();
      case UNION:
        return new UnionParser();
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
      case BASE:
        return new BaseParser();
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
      case DWSIN:
      case DWCOS:
      case DBSIN:
      case DBCOS:
        return trigDefinesParser;
      default:
        return new GenericDirectiveParser(type);
    }
  }

  /**
   * Creates the appropriate class of directive node
   *
   * @param directiveName the name of the directive
   * @param directiveToken
   * @return a directive node with all of its arguments and body
   */
  public static DirectiveNode createDirectiveNode(String directiveName, Token directiveToken) {
    // A few directive enums don't match the source pattern.  We do a manual mapping here.
    try {
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
          node = new BankNode(directiveToken);
          break;
        case ENUM:
          node = new EnumNode(directiveToken);
          break;

        case STRUCT:
          node = new StructNode(directiveToken);
          break;
        case SECTION:
          node = new SectionNode(directiveToken);
          break;
        case MACRO:
          node = new MacroNode(directiveToken);
          break;
        case UNION:
        case NEXTU:
          node = new UnionNode(directiveToken);
          break;
        case MEMORYMAP:
        case ROMBANKMAP:
        case RAMSECTION:
        case GBHEADER:
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
          node = new DirectiveNode(directive, directiveToken, true);
          break;
        default:
          node = new DirectiveNode(directive, directiveToken, false);
      }

      return node;
    } catch (Exception ex) {
      throw new ParseException("invalid directive " + directiveName, ex, directiveToken);
    }
  }

  public static NodeTypes getDirectiveNodeType(AllDirectives directive) {
    return NodeTypes.DIRECTIVE;
  }
}
