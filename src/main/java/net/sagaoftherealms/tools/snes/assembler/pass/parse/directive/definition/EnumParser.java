package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

/** This class parses Enums, Structs, and RAMSECTIONS */
public class EnumParser extends BodyDefinitionParser {

  public EnumParser() {
    super(AllDirectives.ENUM);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var arguments = new EnumArgumentsNode(token);

    var expression = ExpressionParser.expressionNode(parser);

    arguments.put(KEYS.ADDRESS, expression);

    token = parser.getCurrentToken();

    while (token != null && token.getType() != EOL) {

      parser.consume(TokenTypes.LABEL);
      var argument = token.getString().toUpperCase();

      switch (argument) {
        case "ASC":
        case "DESC":
          if (arguments.get(KEYS.ORDINAL) == null) {
            arguments.put(KEYS.ORDINAL, argument, token);
          } else {
            throw new ParseException("The Ordinal of an enum may only be specified once", token);
          }
          break;
        case "EXPORT":
          if (arguments.get(KEYS.EXPORT) == null) {
            arguments.put(KEYS.EXPORT, argument, token);
          } else {
            throw new ParseException("Duplicate Export Token.", token);
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

    return arguments;
  }

  public enum KEYS {
    ORDINAL,
    EXPORT,
    ADDRESS
  }
}
