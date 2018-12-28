package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import static com.google.common.base.Strings.isNullOrEmpty;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionParser.KEYS;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.Token;

public class SectionNode extends DirectiveNode {

  @Override
  public SectionArgumentsNode getArguments() {
    return (SectionArgumentsNode) super.getArguments();
  }

  public SectionNode(Token token) {
    super(AllDirectives.SECTION, token);
  }

  public String getName() {
    return getArguments().get(KEYS.NAME);
  }

  public Integer getMaxSize() {
    return safeInteger(KEYS.SIZE);
  }

  public String getNamespace() {
    return getArguments().get(KEYS.NAMESPACE);
  }

  public Integer getAlignment() {
    return safeInteger(KEYS.ALIGN);
  }

  private Integer safeInteger(KEYS key) {
    String arg = getArguments().get(key);
    if (isNullOrEmpty(arg)) {
      return null;
    }
    return Integer.parseInt(arg);
  }

  public String getAppendTo() {
    return (getArguments().get(KEYS.APPEND_TO));
  }

  public SectionStatus getStatus() {
    return SectionStatus.valueOf(getArguments().get(KEYS.STATUS));
  }

  public boolean isAdvanceOrg() {
    return getArguments().get(KEYS.RETURNORG) != null;
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
