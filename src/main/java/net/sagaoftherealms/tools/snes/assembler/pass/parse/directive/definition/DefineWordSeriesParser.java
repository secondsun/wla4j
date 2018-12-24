package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;

public class DefineWordSeriesParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(AllDirectives.DSW);

  public DefineWordSeriesParser(AllDirectives type) {
    super(type);
    if (!types.contains(type)) {
      throw new IllegalArgumentException(type + " not supported.  Use one of " + types);
    }
  }
}
