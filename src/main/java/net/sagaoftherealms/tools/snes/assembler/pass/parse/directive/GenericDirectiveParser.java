package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import java.util.Optional;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.DirectiveArgumentsValidator;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.ExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class GenericDirectiveParser implements DirectiveParser {

  private final AllDirectives type;

  public GenericDirectiveParser(AllDirectives type) {
    this.type = type;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var token = parser.getCurrentToken();
    var argumentsNode = new DirectiveArgumentsNode(token);

    if (!hasArguments(type)) {
      return argumentsNode;
    }

    var argumentsPattern = type.getPattern().split("\\." + type.getName())[1].trim();
    var validator = new DirectiveArgumentsValidator(argumentsPattern);

    while (token.getType() != TokenTypes.END_OF_INPUT) {
      Optional<Node> potentialNode = validator.accept(token, parser);
      if (potentialNode.isPresent()) {

        var node = potentialNode.get();
        if (node instanceof ExpressionNode) {
          argumentsNode.add((ExpressionNode) node);
        } else {
          argumentsNode.add(new StringExpressionNode(token.getString(), token));
        }
        token = parser.getCurrentToken();
        if (token.getType() == TokenTypes.COMMA) {
          parser.consume(TokenTypes.COMMA);
        }
      } else {
        if (validator.checkHasMore()) {
          throw new ParseException("Invalid argument ", token);
        }
        break;
      }

      token = parser.getCurrentToken();
    }

    parser.consume(TokenTypes.END_OF_INPUT, TokenTypes.EOL);

    if (validator.checkHasMore()) {
      throw new ParseException("Invalid argument ", token);
    }

    return argumentsNode;
  }

  private boolean hasArguments(AllDirectives type) {
    return type.getPattern().split("\\." + type.getName()).length > 1;
  }
}
