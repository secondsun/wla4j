package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.BYT;
import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.BYTE;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.COMMA;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;

public class DefineByteParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(AllDirectives.DB, BYT, BYTE);

  public DefineByteParser(AllDirectives type) {
    super(type);
    if (!types.contains(type)) {
      throw new IllegalArgumentException(type + " not supported.  Use one of " + types);
    }
  }

  /**
   * Bytes are a list of expressions each of which evaluates to something less than 255
   *
   * @param parser the source parser
   * @return the list of bytes to be defined
   */
  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode();

    var token = parser.getCurrentToken();
    while (token.getType() != EOL && token.getType() != END_OF_INPUT) {
      arguments.add(ExpressionParser.expressionNode(parser));
      token = parser.getCurrentToken();
      while (token.getType().equals(COMMA)) {
        parser.consumeAndClear(COMMA);
        token = parser.getCurrentToken();
      }
    }

    if (arguments.size() == 0) {
      throw new ParseException(
          "At least one byte definition is required.", parser.getCurrentToken());
    }

    parser.consumeAndClear(EOL, END_OF_INPUT);

    return arguments;
  }
}
