package net.sagaoftherealms.tools.snes.assembler.analyzer;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractAnalyzer {
    protected final Context context;

    protected AbstractAnalyzer(Context context) {
        this.context = context;
    }


    public abstract List<? extends ErrorNode> checkDirective(DirectiveNode node);

        /**
         * Node must be in the types of validDirectives or a illegal argument exception will be thrown
         *
         * @param node a node
         * @param validDirectives directives
         */
    protected  void enforceDirectiveType(DirectiveNode node, AllDirectives... validDirectives) {
        var directives = List.of(validDirectives);
        if (!directives.contains(node.getDirectiveType())) {
            throw new IllegalArgumentException(String.format("Node was of type %s and expected %s", node.getDirectiveType().getName(), directives.stream()
                    .map(AllDirectives::getName)
                    .collect(Collectors.joining(","))));
        }
    }
}
