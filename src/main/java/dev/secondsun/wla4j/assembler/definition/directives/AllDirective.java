package dev.secondsun.wla4j.assembler.definition.directives;

public class AllDirective extends Directive {

  public AllDirective(String s) {
    super(s.split(" ")[0].replace(".", ""), s);
    if (s.contains("}") && !s.endsWith("}") && s.split("}")[1].contains(",")) {
      throw new IllegalArgumentException("Directives may not have commas after arrays");
    }
  }
}
