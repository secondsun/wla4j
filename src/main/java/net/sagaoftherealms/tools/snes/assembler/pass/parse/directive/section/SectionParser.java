package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.ENDS;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.LABEL;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/**
 * This class parses Enums, Structs
 */
public class SectionParser implements DirectiveParser {

  private final AllDirectives endDirective = ENDS;

  private boolean isBankheader = false;

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    var body = new DirectiveBodyNode();
    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();
    var node = parser.nextNode();

    while (node != null) {
      if (node.getType().equals(NodeTypes.DIRECTIVE)) {

        var directiveNode = (DirectiveNode) node;
        var directive = directiveNode.getDirectiveType();

        if (directive.equals(endDirective)) {
          break;
        }

        switch (directive) {
          case SECTION:
          case RAMSECTION:
          case ORG:
          case ORGA:
          case SLOT:
          case BANK:
            throw new ParseException("You may not use this directive inside a section", token);
          default:
            break;
        }
      }

      if (node.getType() == NodeTypes.LABEL_DEFINITION && isBankheader) {
        throw new ParseException("Labels are not allowed in bankheaders.", token);
      }

      body.addChild(node);
      node = parser.nextNode();
    }

    return body;
  }

  @Override
  public DirectiveArgumentsNode arguments(SourceParser parser) {
    var arguments = new SectionArgumentsNode();

    var token = parser.getCurrentToken();
    parser.consume(TokenTypes.STRING, TokenTypes.LABEL);
    arguments.put(KEYS.NAME, "" + token.getString());

    if (token.getString().equalsIgnoreCase("BANKHEADER")) {
      arguments.put(KEYS.BANKHEADER, token.getString());
      isBankheader = true;
    }

    token = parser.getCurrentToken();

    while (token != null && token.getType() != EOL) {

      var argument = token.getString();

      switch (argument) {
        case "NAMESPACE":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          setStringArgument(token, arguments, KEYS.NAMESPACE, parser);
          parser.consume(TokenTypes.STRING);
          break;
        case "SIZE":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          setIntArgument(token, arguments, KEYS.SIZE, parser);
          parser.consume(TokenTypes.NUMBER);
          break;
        case "ALIGN":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          setIntArgument(token, arguments, KEYS.ALIGN, parser);
          parser.consume(TokenTypes.NUMBER);
          break;
        case "APPENDTO":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          setStringArgument(token, arguments, KEYS.APPEND_TO, parser);
          parser.consume(TokenTypes.LABEL);
          break;
        case "FORCE":
        case "FREE":
        case "SUPERFREE":
        case "SEMIFREE":
        case "SEMISUBFREE":
        case "OVERWRITE":
          parser.consume(LABEL);
          setStringArgument(token, arguments, KEYS.STATUS, parser);
          break;
        case "RETURNORG":
          parser.consume(LABEL);
          setStringArgument(token, arguments, KEYS.RETURNORG, parser);
          break;
        default:
          throw new ParseException("Unknown Argument.", token);
      }

      token = parser.getCurrentToken();
    }

    if (token == null) {
      throw new ParseException("Unexpected End of input", token);
    }
    parser.consumeAndClear(TokenTypes.EOL);
    return arguments;
  }

  private void setIntArgument(Token token, SectionArgumentsNode arguments, KEYS key,
      SourceParser parser) {
    if (arguments.get(key) == null) {
      arguments.put(key, TokenUtil.getInt(token) + ""); // TYPECHECK

    } else {
      throw new ParseException(
          "The namespace of an section may only be specified once", token);
    }
  }

  private void setStringArgument(Token token, SectionArgumentsNode arguments, KEYS key,
      SourceParser parser) {
    if (arguments.get(key) == null) {
      arguments.put(key, token.getString());
    } else {
      throw new ParseException(
          "Arguments of a section may only be specified once", token);
    }
  }

  public enum KEYS {
    NAME,
    BANKHEADER,
    NAMESPACE,
    SIZE,
    ALIGN,
    STATUS,
    APPEND_TO,
    RETURNORG;
  }
}
