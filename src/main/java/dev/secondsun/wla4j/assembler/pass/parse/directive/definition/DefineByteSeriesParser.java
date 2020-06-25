package dev.secondsun.wla4j.assembler.pass.parse.directive.definition;

import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.DS;
import static dev.secondsun.wla4j.assembler.definition.directives.AllDirectives.DSB;

import java.util.Arrays;
import java.util.List;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.GenericDirectiveParser;

public class DefineByteSeriesParser extends GenericDirectiveParser {

  private static final List<AllDirectives> types = Arrays.asList(DS, DSB);

  public DefineByteSeriesParser(AllDirectives type) {
    super(type);
    if (!types.contains(type)) {
      throw new IllegalArgumentException(type + " not supported.  Use one of " + types);
    }
  }
}
