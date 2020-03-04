package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

public class BankSizeAnalyzer extends AbstractAnalyzer {

  public BankSizeAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    var errors = new ArrayList<ErrorNode>();
    if (node.getDirectiveType().equals(AllDirectives.ROMBANKSIZE)
        || node.getDirectiveType().equals(AllDirectives.BANKSIZE)) {
      int bankSize = node.getArguments().getInt(0);
      if (bankSize < 0) {
        errors.add(
            new ErrorNode(
                node.getArguments().getChildren().get(0).getSourceToken(),
                new ParseException(
                    "Expected positive size",
                    node.getArguments().getChildren().get(0).getSourceToken())));
      }

      if (super.context.getBankSizeSet()) {
        errors.add(
            new ErrorNode(
                node.getSourceToken(),
                new ParseException("Bank Size may only be set once", node.getSourceToken())));
      }

      context.setBankSize(bankSize);
      context.setBankSizeSet(true);

      return errors;
    }
    throw new IllegalArgumentException(
        "Exptected a rombanksize or banksize directive, got  " + node.toString());
  }
}
