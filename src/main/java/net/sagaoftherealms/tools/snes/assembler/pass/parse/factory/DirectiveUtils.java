package net.sagaoftherealms.tools.snes.assembler.pass.parse.factory;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.EnumParser;

public final class DirectiveUtils {
    private DirectiveUtils() {}

    public static DirectiveParser getParser(AllDirectives type) {
        switch (type) {
            case ENUM:
                return new EnumParser();
            default:
                return new GenericDirectiveParser(type);

        }
    }

    public static DirectiveNode createDirectiveNode(String directiveName) {
        AllDirectives directive = AllDirectives.valueOf(directiveName);
        var node = new DirectiveNode(directive);
        return node;
    }

    public static NodeTypes getDirectiveNodeType(AllDirectives directive) {
        return directive == AllDirectives.ENUM?NodeTypes.ENUM:NodeTypes.DIRECTIVE;
    }
}
