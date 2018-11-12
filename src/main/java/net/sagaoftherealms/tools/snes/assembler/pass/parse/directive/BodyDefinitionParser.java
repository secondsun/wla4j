package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.factory.GenericDirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenUtil;

import java.util.EnumSet;

import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.END_OF_INPUT;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.EOL;
import static net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes.NUMBER;

/**
 * This class parses the body of directives which have definitions.
 * <p>
 * IE RAMSECTION, ENUM, and STRUCT
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
        var token = parser.getCurrentToken();

        while (token != null && EOL.equals(token.getType())) {
            parser.advanceToken();
            token = parser.getCurrentToken();
        }

        while (token != null && !endDirective.getPattern().startsWith(token.getString())) {//End on ENDE
            //Expect the token to be the first label
            if (token.getType() != TokenTypes.LABEL) {
                throw new ParseException("Label expected in enum.", token);
            }
            var bodyNode = new DefinitionNode(TokenUtil.getLabelName(token));

            token = nextTokenWithType(TokenTypes.LABEL, parser);

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
                    token = nextTokenWithType(TokenTypes.NUMBER, parser);
                    bodyNode.setSize(TokenUtil.getInt(token));
                    break;
                case "DSW":

                    token = nextTokenWithType(TokenTypes.NUMBER, parser);

                    bodyNode.setSize(TokenUtil.getInt(token) * 2);
                    break;
                case "INSTANCEOF":
                    token = nextTokenWithType(TokenTypes.LABEL, parser);
                    bodyNode.setStructName(TokenUtil.getLabelName(token));
                    bodyNode.setSize(1);
                    token = nextTokenWithType(EnumSet.of(TokenTypes.LABEL, TokenTypes.DIRECTIVE, TokenTypes.NUMBER), parser);
                    if (NUMBER.equals(token.getType())) {
                        bodyNode.setSize(TokenUtil.getInt(token));
                    }
                    break;
                default:
                    throw new ParseException("Unexpected type.", token);
            }

            token = nextTokenWithType(EnumSet.of(TokenTypes.LABEL, TokenTypes.DIRECTIVE, TokenTypes.END_OF_INPUT), parser);
            body.addChild(bodyNode);
            if (token.getType().equals(END_OF_INPUT)) {
                break;
            }
        }

        return body;
    }

    private Token nextTokenWithType(TokenTypes type, SourceParser parser) {
        return this.nextTokenWithType(EnumSet.of(type), parser);
    }

    private Token nextTokenWithType(EnumSet<TokenTypes> types, SourceParser parser) {
        parser.advanceToken();
        var token = parser.getCurrentToken();

        while (token != null && EOL.equals(token.getType())) {
            parser.advanceToken();
            token = parser.getCurrentToken();
        }
        if (token == null || !types.contains(token.getType())) {
            throw new ParseException(types.toString() + " expected.", token);
        }

        return token;

    }


}
