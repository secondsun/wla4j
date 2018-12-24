package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;

import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.DW;
import static net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives.WORD;

import java.util.Arrays;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.GenericDirectiveParser;

public class DefineWordParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(DW, WORD);

  public DefineWordParser(AllDirectives type) {
    super(type);
    if (!types.contains(type)) {
      throw new IllegalArgumentException(type + " not supported.  Use one of " + types);
    }
  }
}
