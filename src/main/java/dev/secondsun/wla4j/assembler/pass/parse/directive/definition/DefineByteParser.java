package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.ASC;
import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.BYT;
import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.BYTE;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.COMMA;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.EOL;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import java.util.Arrays;
import java.util.List;

public class DefineByteParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(AllDirectives.DB, BYT, BYTE, ASC);

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
    var token = parser.getCurrentToken();
    DirectiveArgumentsNode arguments = new DirectiveArgumentsNode(token);

    while (token.getType() != EOL && token.getType() != END_OF_INPUT) {
      arguments.add(ExpressionParser.expressionNode(parser));
      token = parser.getCurrentToken();
      while (token.getType().equals(COMMA)) {
        parser.consume(COMMA);
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
