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
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

/** This class parses Enums, Structs */
public class SectionParser implements DirectiveParser {

  private final AllDirectives endDirective = ENDS;

  private boolean isBankheader = false;

  @Override
  public DirectiveBodyNode body(SourceParser parser) {
    var body = new DirectiveBodyNode();
    parser.clearWhiteSpaceTokens();
    var token = parser.getCurrentToken();
    var node = parser.nextNode();

    while (node != null ) {
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

      if (node.getType() == NodeTypes.LABEL && isBankheader) {
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
          if (arguments.get(KEYS.NAMESPACE) == null) {
            arguments.put(KEYS.NAMESPACE, token.getString());
            parser.consume(TokenTypes.STRING);
          } else {
            throw new ParseException(
                "The namespace of an section may only be specified once", token);
          }
          break;
        case "SIZE":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(KEYS.SIZE) == null) {
            arguments.put(KEYS.SIZE, TokenUtil.getInt(token) + ""); // TYPECHECK
            parser.consume(TokenTypes.NUMBER);
          } else {
            throw new ParseException(
                "The namespace of an section may only be specified once", token);
          }
          break;
        case "ALIGN":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(KEYS.ALIGN) == null) {
            arguments.put(KEYS.ALIGN, TokenUtil.getInt(token) + ""); // TYPECHECK
            parser.consume(TokenTypes.NUMBER);
          } else {
            throw new ParseException(
                "The namespace of an section may only be specified once", token);
          }
          break;
        case "APPENDTO":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(KEYS.APPEND_TO) == null) {
            arguments.put(KEYS.APPEND_TO, token.getString()); // TYPECHECK
            parser.consume(TokenTypes.LABEL);
          } else {
            throw new ParseException(
                "The namespace of an section may only be specified once", token);
          }
          break;
        case "FORCE":
        case "FREE":
        case "SUPERFREE":
        case "SEMIFREE":
        case "SEMISUBFREE":
        case "OVERWRITE":
          parser.consume(LABEL);
          token = parser.getCurrentToken();
          if (arguments.get(KEYS.STATUS) == null) {
            arguments.put(KEYS.STATUS, argument); // TYPECHECK
          } else {
            throw new ParseException(
                "The namespace of an section may only be specified once", token);
          }
          break;
        case "RETURNORG":
          parser.consume(LABEL);
          if (arguments.get(KEYS.RETURNORG) == null) {
            arguments.put(KEYS.RETURNORG, argument);
          } else {
            throw new ParseException("Duplicate RETURNORG Token.", token);
          }
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
