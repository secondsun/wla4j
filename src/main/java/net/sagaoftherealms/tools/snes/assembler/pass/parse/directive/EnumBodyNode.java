package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

public class EnumBodyNode extends Node {
    private final String label;

    public EnumBodyNode(String label) {
        super(NodeTypes.DIRECTIVE_BODY);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
