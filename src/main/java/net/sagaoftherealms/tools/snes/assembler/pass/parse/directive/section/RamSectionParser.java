package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.ENDS;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.ALIGN;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.APPEND_TO;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.BANK;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.NAME;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.SLOT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.BodyDefinitionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class RamSectionParser extends BodyDefinitionParser {

  private final AllDirectives endDirective = ENDS;

  public RamSectionParser() {
    super(AllDirectives.RAMSECTION);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var arguments = new RamsectionArgumentsNode();

    var token = parser.getCurrentToken();
    parser.consume(TokenTypes.STRING, TokenTypes.LABEL);
    arguments.put(NAME, "" + token.getString());

    token = parser.getCurrentToken();

    while (token != null && token.getType() != EOL) {

      var argument = token.getString();

      switch (argument) {
        case "BANK":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(BANK) == null) {
            arguments.put(BANK, TokenUtil.getInt(token) + "");
            parser.consume(TokenTypes.NUMBER);
          } else {
            throw new ParseException("Bank may only be specified once", token);
          }
          break;
        case "SLOT":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(SLOT) == null) {
            arguments.put(SLOT, TokenUtil.getInt(token) + ""); // TYPECHECK
            parser.consume(TokenTypes.NUMBER);
          } else {
            throw new ParseException("The slot of an section may only be specified once", token);
          }
          break;
        case "ALIGN":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(ALIGN) == null) {
            arguments.put(ALIGN, TokenUtil.getInt(token) + ""); // TYPECHECK
            parser.consume(TokenTypes.NUMBER);
          } else {
            throw new ParseException(
                "The alignment of an section may only be specified once", token);
          }
          break;
        case "APPENDTO":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(APPEND_TO) == null) {
            arguments.put(APPEND_TO, token.getString()); // TYPECHECK
            parser.consume(TokenTypes.LABEL);
          } else {
            throw new ParseException(
                "The appendto of an section may only be specified once", token);
          }
          break;
        default:
          throw new ParseException("Unknown Argument.", token);
      }

      token = parser.getCurrentToken();
    }

    if (token == null) {
      throw new ParseException("Unexpected End of input", token);
    }
    parser.consumeAndClear(TokenTypes.EOL);
    return arguments;
  }
}
