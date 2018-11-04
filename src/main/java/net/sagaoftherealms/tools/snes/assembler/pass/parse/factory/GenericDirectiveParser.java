package net.sagaoftherealms.tools.snes.assembler.pass.parse.factory;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveParser;

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
        var argumentsPattern = type.getPattern();
        var argumentsNode = new DirectiveArgumentsNode();
        return null;
    }
}
