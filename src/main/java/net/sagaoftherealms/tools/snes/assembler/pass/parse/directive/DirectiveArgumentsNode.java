package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

import java.util.HashMap;
import java.util.Map;

public class DirectiveArgumentsNode extends Node {
    private final Map<EnumParser.KEYS, String> arguments = new HashMap<>();

    public DirectiveArgumentsNode() {
        super(NodeTypes.DIRECTIVE_ARGUMENTS);
    }

    public String get(EnumParser.KEYS argumentKey) {
        return arguments.get(argumentKey);
    }

    public DirectiveArgumentsNode put(EnumParser.KEYS argumentKey, String argumentValue) {
        arguments.put(argumentKey, argumentValue);
        return this;
    }


}
