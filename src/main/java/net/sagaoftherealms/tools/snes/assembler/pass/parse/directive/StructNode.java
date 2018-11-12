package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;

public class StructNode extends DirectiveNode {

    public StructNode() {
        super(AllDirectives.STRUCT);
    }

    public String getName() {
        return getArguments().get(0).trim();
    }

}
