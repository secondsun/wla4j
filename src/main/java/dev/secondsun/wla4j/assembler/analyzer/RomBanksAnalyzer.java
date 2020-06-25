package dev.secondsun.wla4j.assembler.analyzer;

import java.util.ArrayList;
import java.util.List;

import dev.secondsun.wla4j.assembler.pass.parse.ErrorNode;
import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.ParseException;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;

public class RomBanksAnalyzer extends AbstractAnalyzer {

  public RomBanksAnalyzer(Context context) {
    super(context);
  }

  @Override
  public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
    enforceDirectiveType(node, AllDirectives.ROMBANKS);
    var errors = new ArrayList<ErrorNode>();
    if (context.getBankSize() <= 0) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException("Banksize must be defined", node.getSourceToken())));
    }

    if (context.getRomBanksDefined()) {
      errors.add(
          new ErrorNode(
              node.getSourceToken(),
              new ParseException("Banksize must be defined only once", node.getSourceToken())));
    }

    if (errors.isEmpty()) {
      context.setRomBanks(node.getArguments().getInt(0));
      context.setRomBanksDefined(true);
      context.createRomBanks();
    }
    return errors;
  }
}
