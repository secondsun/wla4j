package dev.secondsun.wla4j.assembler.pass.parse.directive.section;

import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.ENDS;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.EOL;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.LABEL;
import static dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes.STRING;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.NodeTypes;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.SourceParser;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveBodyNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveArgumentsNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveParser;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenTypes;
import dev.secondsun.wla4j.assembler.pass.scan.token.TokenUtil;

/** This class parses Enums, Structs */
public class SectionParser implements DirectiveParser {

  private static final AllDirectives endDirective = ENDS;

  private boolean isBankheader = false;

  @Override
  public DirectiveBodyNode body(SourceParser parser, Token initialToken) {

    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();
    var body = new DirectiveBodyNode(initialToken);
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
    var localToken = parser.getCurrentToken();
    var arguments = new SectionArgumentsNode(localToken);

    parser.consume(TokenTypes.STRING, TokenTypes.LABEL);
    arguments.put(KEYS.NAME, "" + localToken.getString(), localToken);

    if (localToken.getString().equalsIgnoreCase("BANKHEADER")) {
      arguments.put(KEYS.BANKHEADER, localToken.getString(), localToken);
      isBankheader = true;
    }

    localToken = parser.getCurrentToken();

    while (localToken != null && localToken.getType() != EOL) {

      var argument = localToken.getString();

      switch (argument.toUpperCase()) {
        case "NAMESPACE":
          parser.consume(LABEL);
          localToken = parser.getCurrentToken();
          setStringArgument(localToken, arguments, KEYS.NAMESPACE);
          parser.consume(TokenTypes.STRING);
          break;
        case "SIZE":
          parser.consume(LABEL);
          localToken = parser.getCurrentToken();
          setIntArgument(localToken, arguments, KEYS.SIZE);
          parser.consume(TokenTypes.NUMBER);
          break;
        case "ALIGN":
          parser.consume(LABEL);
          localToken = parser.getCurrentToken();
          setIntArgument(localToken, arguments, KEYS.ALIGN);
          parser.consume(TokenTypes.NUMBER);
          break;
        case "APPENDTO":
          parser.consume(LABEL);
          localToken = parser.getCurrentToken();
          setStringArgument(localToken, arguments, KEYS.APPEND_TO);
          parser.consume(TokenTypes.LABEL, STRING);
          break;
        case "FORCE":
        case "FREE":
        case "SUPERFREE":
        case "SEMIFREE":
        case "SEMISUBFREE":
        case "OVERWRITE":
          parser.consume(LABEL);
          setStringArgument(localToken, arguments, KEYS.STATUS);
          break;
        case "RETURNORG":
          parser.consume(LABEL);
          setStringArgument(localToken, arguments, KEYS.RETURNORG);
          break;
        default:
          throw new ParseException("Unknown Argument.", localToken);
      }

      localToken = parser.getCurrentToken();
    }

    if (localToken == null) {
      throw new ParseException("Unexpected End of input", localToken);
    }
    parser.consumeAndClear(TokenTypes.EOL);
    return arguments;
  }

  private void setIntArgument(Token token, SectionArgumentsNode arguments, KEYS key) {
    if (arguments.get(key) == null) {
      arguments.put(key, TokenUtil.getInt(token) + "", token); // TYPECHECK

    } else {
      throw new ParseException("The namespace of an section may only be specified once", token);
    }
  }

  private void setStringArgument(Token token, SectionArgumentsNode arguments, KEYS key) {
    if (arguments.get(key) == null) {
      arguments.put(key, token.getString(), token);
    } else {
      throw new ParseException("Arguments of a section may only be specified once", token);
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
