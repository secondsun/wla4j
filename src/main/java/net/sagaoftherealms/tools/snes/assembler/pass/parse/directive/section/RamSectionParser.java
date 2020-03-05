package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.BodyDefinitionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

public class RamSectionParser extends BodyDefinitionParser {

  public RamSectionParser() {
    super(AllDirectives.RAMSECTION);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var arguments = new RamsectionArgumentsNode(token);

    parser.consume(TokenTypes.STRING, TokenTypes.LABEL);
    arguments.put(RamsectionArguments.NAME, "" + token.getString(), token);

    token = parser.getCurrentToken();

    while (token != null && token.getType() != EOL) {

      var argument = token.getString().toUpperCase();

      switch (argument) {
        case "BANK":
          consumeInt(RamsectionArguments.BANK, arguments, parser);
          break;
        case "SLOT":
          consumeInt(RamsectionArguments.SLOT, arguments, parser);
          break;
        case "ALIGN":
          consumeInt(RamsectionArguments.ALIGN, arguments, parser);
          break;
        case "APPENDTO":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(RamsectionArguments.APPEND_TO) == null) {
            arguments.put(RamsectionArguments.APPEND_TO, token.getString(), token); // TYPECHECK
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
      arguments.put(argument, TokenUtil.getInt(token) + "", token);
      parser.consume(TokenTypes.NUMBER);
    } else {
      throw new ParseException("Arguments may only be specified once", token);
    }
  }
}
