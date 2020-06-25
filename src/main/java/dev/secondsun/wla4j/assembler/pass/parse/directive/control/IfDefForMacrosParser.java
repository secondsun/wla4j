package dev.secondsun.wla4j.assembler.pass.parse.directive.control;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.StringExpressionNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;

public class IfDefForMacrosParser extends IfParser {

  public IfDefForMacrosParser(AllDirectives type) {
    super(type);
    if (!(type == AllDirectives.IFDEFM || type == AllDirectives.IFNDEFM)) {
      throw new IllegalArgumentException("Invalid Type." + type);
    }
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var label = parser.getCurrentToken();
    var node = new DirectiveArgumentsNode(label);

    if (!label.getString().matches("\\\\\\d+")) {
      throw new ParseException("Macro labels should be in the form \\d+", label);
    }

    parser.consume(TokenTypes.LABEL);
    parser.consume(TokenTypes.EOL);

    node.add(new StringExpressionNode(label.toString(), label));

    return node;
  }
}
