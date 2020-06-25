package dev.secondsun.wla4j.assembler.pass.parse.directive.incbin;

import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.EOL;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.incbin.IncBinArgumentsNode.Arguments;
import dev.secondsun.wla4j.assembler.pass.parse.expression.ExpressionParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

public class IncbinParser extends GenericDirectiveParser {

  public IncbinParser() {
    super(AllDirectives.INCBIN);
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var arguments = new IncBinArgumentsNode(token);

    parser.consume(TokenTypes.STRING);

    arguments.put(Arguments.NAME, new StringExpressionNode(token.getString(), token));

    token = parser.getCurrentToken();
    while (token != null && token.getType() != EOL && token.getType() != END_OF_INPUT) {
      parser.consume(TokenTypes.LABEL);
      var argument = token.getString().toUpperCase();

      switch (argument) {
        case "READ":
          if (arguments.get(Arguments.READ) == null) {
            arguments.put(Arguments.READ, ExpressionParser.expressionNode(parser));
          } else {
            throw new ParseException("Read may only be specified once", token);
          }
          break;
        case "SKIP":
          if (arguments.get(Arguments.SKIP) == null) {
            arguments.put(Arguments.SKIP, ExpressionParser.expressionNode(parser));
          } else {
            throw new ParseException("Skip may only be specified once", token);
          }
          break;
        case "FSIZE":
          if (arguments.get(Arguments.FSIZE) == null) {
            arguments.put(Arguments.FSIZE, ExpressionParser.expressionNode(parser));
          } else {
            throw new ParseException("FSIZE may only be specified once", token);
          }
          break;
        case "FILTER":
          if (arguments.get(Arguments.FILTER) == null) {
            arguments.put(Arguments.FILTER, ExpressionParser.expressionNode(parser));
          } else {
            throw new ParseException("Filter may only be specified once", token);
          }
          break;
        case "SWAP":
          if (arguments.get(Arguments.SWAP) == null) {
            arguments.put(Arguments.SWAP, new StringExpressionNode(argument, token));
          } else {
            throw new ParseException("Duplicate Swap Token.", token);
          }
          break;
        default:
          throw new ParseException("Unknown Argument.", token);
      }
      token = parser.getCurrentToken();
    }
    parser.consume(EOL, END_OF_INPUT);
    return arguments;
  }
}
