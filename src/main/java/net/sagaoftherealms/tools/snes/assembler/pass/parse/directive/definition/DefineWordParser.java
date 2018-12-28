package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.DW;
import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.WORD;
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
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class DefineWordParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(DW, WORD);

  public DefineWordParser(AllDirectives type) {
    super(type);
    if (!types.contains(type)) {
      throw new IllegalArgumentException(type + " not supported.  Use one of " + types);
    }
  }

  /**
   * Bytes are a list of expressions each of which evaluates to something less than 2^16
   *
   * @param parser the source parser
   * @return the list of words to be defined
   */
  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode(token);

    while (token.getType() != EOL && token.getType() != END_OF_INPUT) {
      if (token.getType().equals(TokenTypes.STRING)) {
        throw new ParseException("Strings are not allowed in DW/WORD", token);
      }
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
