package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/**
 * This class parses the body of directives which have definitions.
 *
 * <p>IE RAMSECTION, ENUM, and STRUCT
 */
public abstract class BodyDefinitionParser extends GenericDirectiveParser {

  private final AllDirectives endDirective;

  public BodyDefinitionParser(AllDirectives type) {
    super(type);
    switch (type) {
      case ENUM:
        endDirective = AllDirectives.ENDE;
        break;
      case STRUCT:
        endDirective = AllDirectives.ENDST;
        break;
      default:
        throw new IllegalArgumentException("Unsupported type" + type);
    }
  }

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    var body = new DirectiveBodyNode();
    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();

    while (token != null
        && !endDirective.getPattern().startsWith(token.getString())) { // End on ENDE
      // Expect the token to be the first label
      parser.consumeAndClear(TokenTypes.LABEL);

      var bodyNode = new DefinitionNode(TokenUtil.getLabelName(token));

      token = parser.getCurrentToken();

      parser.consumeAndClear(TokenTypes.LABEL);

      switch (token.getString().toUpperCase()) {
        case "DB":
        case "BYTE":
        case "BYT":
          bodyNode.setSize(1);
          break;
        case "DW":
        case "WORD":
          bodyNode.setSize(2);
          break;
        case "DS":
        case "DSB":
          token = parser.getCurrentToken();
          parser.consumeAndClear(TokenTypes.NUMBER);
          bodyNode.setSize(TokenUtil.getInt(token));
          break;
        case "DSW":
          token = parser.getCurrentToken();
          parser.consumeAndClear(TokenTypes.NUMBER);
          bodyNode.setSize(TokenUtil.getInt(token) * 2);
          break;
        case "INSTANCEOF":
          token = parser.getCurrentToken();
          parser.consumeAndClear(TokenTypes.LABEL);

          bodyNode.setStructName(TokenUtil.getLabelName(token));
          bodyNode.setSize(1);
          token = parser.getCurrentToken();

          if (NUMBER.equals(token.getType())) {
            bodyNode.setSize(TokenUtil.getInt(token));
            parser.consumeAndClear(TokenTypes.NUMBER);
          }
          break;
        default:
          throw new ParseException("Unexpected type.", token);
      }

      token = parser.getCurrentToken();

      body.addChild(bodyNode);
      if (token.getType().equals(END_OF_INPUT)) {
        break;
      }
    }

    parser.consumeAndClear(TokenTypes.DIRECTIVE); // consume the .END? directives
    return body;
  }
}
