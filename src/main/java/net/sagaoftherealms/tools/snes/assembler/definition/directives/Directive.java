package net.sagaoftherealms.tools.snes.assembler.definition.directives;

public abstract class Directive {

  private final String name;
  private final String pattern;

  public Directive(String name, String pattern) {
    this.name = name;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public String getPattern() {
    return pattern;
  }
}
