package dev.secondsun.wla4j.assembler.analyzer;

import dev.secondsun.wla4j.assembler.pass.parse.ErrorNode;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import java.util.ArrayList;
import java.util.List;

public class SNESNativeVectorAnalyzer extends AbstractAnalyzer {

  protected SNESNativeVectorAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    var errors = new ArrayList<ErrorNode>();

    if (context.getSnesRomMode() == null) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException(
                  "Rom mode must be set before SNESNATIVEVECTOR", node.getSourceToken())));
    }
    if (context.getSnesNativeVector()) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException("SNESNATIVEVECTOR may only be set once", node.getSourceToken())));
    } else {
      context.setSnesNativeVector(true);
    }

    return errors;
  }
}
