package net.sagaoftherealms.tools.snes.assembler.pass.parse.factory;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.EnumParser;

public final class DirectiveUtils {
    private DirectiveUtils() {}

    public static DirectiveParser getParser(NodeTypes type) {
        switch (type) {
            case ENUM:
                return new EnumParser();
            case DIRECTIVE_ARGUMENTS:
            case DIRECTIVE_BODY:
            default:
                throw new IllegalArgumentException(type + " is not supported");

        }
    }

}
