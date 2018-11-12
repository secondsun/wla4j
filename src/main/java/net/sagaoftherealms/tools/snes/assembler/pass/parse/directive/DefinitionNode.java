package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;

import java.util.Optional;

/**
 * This class represents a label definition.
 *
 * Labels have a label (the name) and a size in bytes
 *
 */
public class DefinitionNode extends Node {
    private final String label;
    private String structName;
    private int size;

    public DefinitionNode(String label) {
        super(NodeTypes.DIRECTIVE_BODY);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Sets the size in bytes
     * @param size number of bytes
     */
    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public Optional<String> getStructName() {
        return Optional.ofNullable(structName);
    }

    public void setStructName(String structName) {
        this.structName = structName;
    }
}
