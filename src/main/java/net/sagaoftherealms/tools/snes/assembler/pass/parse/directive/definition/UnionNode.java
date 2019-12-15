package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StringExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

import java.util.Optional;

public class UnionNode extends DirectiveNode {

    Optional<UnionNode> nextUnion = Optional.empty();

    public UnionNode(Token token) {
        super(AllDirectives.UNION, token, true);
    }


    public String getName() {
        var idNode = (StringExpressionNode) getArguments().getChildren().get(0);
        return idNode.evaluate().trim();
    }

    public Optional<UnionNode> nextUnion() {
        return nextUnion;
    }

    public void setNextUnion(UnionNode next) {
        nextUnion = Optional.of(next);
    }

}
