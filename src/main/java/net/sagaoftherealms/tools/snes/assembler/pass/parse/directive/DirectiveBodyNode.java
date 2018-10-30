package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes.DIRECTIVE_BODY;

public class DirectiveBodyNode extends Node {
    public DirectiveBodyNode() {
        super(DIRECTIVE_BODY);
    }
}
