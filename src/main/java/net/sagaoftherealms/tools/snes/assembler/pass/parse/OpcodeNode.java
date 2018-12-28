package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import java.util.stream.Collectors;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class OpcodeNode extends Node {

  private final Token token;

  public OpcodeNode(Token token) {
    super(NodeTypes.OPCODE, token);
    this.token = token;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(token.getString());
    builder
        .append("(")
        .append(getChildren().stream().map(Object::toString).collect(Collectors.joining(", ")))
        .append(")");
    return builder.toString();
  }
}
