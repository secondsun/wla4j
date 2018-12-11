package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.DirectiveArgumentsValidator;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;

public class GenericDirectiveParser implements DirectiveParser {

  private final AllDirectives type;

  public GenericDirectiveParser(AllDirectives type) {
    this.type = type;
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    return new DirectiveBodyNode();
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var argumentsNode = new DirectiveArgumentsNode();
    
    if (!hasArguments(type)) {
      return  argumentsNode;
    }
    
    var argumentsPattern = type.getPattern().split("\\." + type.getName())[1].trim();
    

    var validator = new DirectiveArgumentsValidator(argumentsPattern);

    var token = parser.getCurrentToken();
    while (token.getType() != TokenTypes.END_OF_INPUT) {
      if (validator.accept(token)) {
        if (token.getType() != TokenTypes.COMMA) {
          argumentsNode.add(token.getString());
        }
      } else {
        if (validator.checkHasMore()) {
          throw new ParseException("Invalid argument ", token);
        }
        break;
      }
      parser.advanceToken();
      token = parser.getCurrentToken();
    }

    parser.advanceToken();//Consume EOL token

    if (validator.checkHasMore()) {
      throw new ParseException("Invalid argument ", token);
    }

    return argumentsNode;
  }

  private boolean hasArguments(AllDirectives type) {
    return type.getPattern().split("\\." + type.getName()).length > 1;
  }
}
