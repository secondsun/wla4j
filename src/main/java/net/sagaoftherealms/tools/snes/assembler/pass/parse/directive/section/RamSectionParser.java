package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

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
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class RamSectionParser extends BodyDefinitionParser {

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
          consumeInt(BANK, arguments, parser);
          break;
        case "SLOT":
          consumeInt(SLOT, arguments, parser);
          break;
        case "ALIGN":
          consumeInt(ALIGN, arguments, parser);
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

  private void consumeInt(
      RamsectionArguments argument, RamsectionArgumentsNode arguments, SourceParser parser) {
    parser.consume(LABEL);
    var token = parser.getCurrentToken();
    if (arguments.get(argument) == null) {
      arguments.put(argument, TokenUtil.getInt(token) + "");
      parser.consume(TokenTypes.NUMBER);
    } else {
      throw new ParseException("Arguments may only be specified once", token);
    }
  }
}
