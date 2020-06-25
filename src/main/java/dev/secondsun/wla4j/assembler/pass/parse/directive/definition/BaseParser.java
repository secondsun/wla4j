package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.BASE;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.EOL;

import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;

public class BaseParser extends GenericDirectiveParser {

  public BaseParser() {
    super(BASE);
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

    arguments.add(ExpressionParser.expressionNode(parser));

    parser.consumeAndClear(EOL, END_OF_INPUT);

    return arguments;
  }
}
