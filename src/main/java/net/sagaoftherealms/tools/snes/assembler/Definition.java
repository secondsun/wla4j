package net.sagaoftherealms.tools.snes.assembler;

public class Definition {

  final Object value;
  final Defines.DefinitionType type;

  public Definition(Object value, Defines.DefinitionType type) {
    this.value = value;
    this.type = type;
  }
}
