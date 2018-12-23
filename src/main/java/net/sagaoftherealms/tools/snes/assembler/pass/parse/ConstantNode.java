package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.Arrays;
import java.util.List;

public class ConstantNode extends Node {

  private static final List<NodeTypes> CONSTANT_TYPES = Arrays.asList(NodeTypes.NUMERIC_CONSTANT);
  private String value;

  public ConstantNode(NodeTypes nodeType) {
    super(nodeType);
    if (!CONSTANT_TYPES.contains(nodeType)) {
      throw new IllegalArgumentException(nodeType + " not in " + CONSTANT_TYPES);
    }
  }

  public ConstantNode(int value) {
    super(NodeTypes.NUMERIC_CONSTANT);
    this.value = value + "";
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public int getValueAsInt() {
    return Integer.parseInt(value);
  }
}
