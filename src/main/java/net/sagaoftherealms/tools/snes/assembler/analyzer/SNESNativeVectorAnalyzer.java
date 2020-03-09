package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

public class SNESNativeVectorAnalyzer extends AbstractAnalyzer {

  protected SNESNativeVectorAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    var errors = new ArrayList<ErrorNode>();

    if (context.getSnesRomMode() == null) {
      errors.add(new ErrorNode(node.getSourceToken(),
          new ParseException("Rom mode must be set before SNESNATIVEVECTOR",
              node.getSourceToken())));
    }
    if (context.getSnesNativeVector()) {
      errors.add(new ErrorNode(node.getSourceToken(),
          new ParseException("SNESNATIVEVECTOR may only be set once", node.getSourceToken())));
    } else {
      context.setSnesNativeVector(true);
    }

    return errors;
  }
}
