package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node {
    public NodeTypes getType() {
        return NodeTypes.ENUM;
    }

    List<Node> children = new ArrayList<>();

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }


}
