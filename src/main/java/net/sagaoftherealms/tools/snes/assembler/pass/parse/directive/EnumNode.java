package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class EnumNode extends DirectiveNode {

    public EnumNode() {
        super(AllDirectives.ENUM);
    }

    public String getAddress() {
        return ((EnumArgumentsNode)getArguments()).get(EnumParser.KEYS.ADDRESS);
    }

}
