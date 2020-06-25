package dev.secondsun.wla4j.assembler.pass.parse.directive.section;

import dev.secondsun.wla4j.assembler.definition.directives.AllDirectives;
import dev.secondsun.wla4j.assembler.pass.parse.directive.DirectiveNode;
import dev.secondsun.wla4j.assembler.pass.scan.token.Token;

public class SectionNode extends DirectiveNode {

  @Override
  public SectionArgumentsNode getArguments() {
    return (SectionArgumentsNode) super.getArguments();
  }

  public SectionNode(Token token) {
    super(AllDirectives.SECTION, token, true);
  }

  public String getName() {
    return getArguments().get(SectionParser.KEYS.NAME);
  }

  public Integer getMaxSize() {
    return safeInteger(SectionParser.KEYS.SIZE);
  }

  public String getNamespace() {
    return getArguments().get(SectionParser.KEYS.NAMESPACE);
  }

  public Integer getAlignment() {
    return safeInteger(SectionParser.KEYS.ALIGN);
  }

  private Integer safeInteger(SectionParser.KEYS key) {
    String arg = getArguments().get(key);
    if (arg == null || arg.isEmpty()) {
      return null;
    }
    return Integer.parseInt(arg);
  }

  public String getAppendTo() {
    return (getArguments().get(SectionParser.KEYS.APPEND_TO));
  }

  public SectionStatus getStatus() {
    return SectionStatus.valueOf(getArguments().get(SectionParser.KEYS.STATUS));
  }

  public boolean isAdvanceOrg() {
    return getArguments().get(SectionParser.KEYS.RETURNORG) != null;
  }

  public enum SectionStatus {
    FORCE,
    FREE,
    SUPERFREE,
    SEMIFREE,
    SEMISUBFREE,
    OVERWRITE,
    BANKHEADER
  }
}
