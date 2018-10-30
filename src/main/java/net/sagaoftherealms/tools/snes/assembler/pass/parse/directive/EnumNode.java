package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class EnumNode extends DirectiveNode {

    public EnumNode() {
        super(NodeTypes.ENUM);
    }

    public String getAddress() {
        return getArguments().get(EnumParser.KEYS.ADDRESS);
    }

}
