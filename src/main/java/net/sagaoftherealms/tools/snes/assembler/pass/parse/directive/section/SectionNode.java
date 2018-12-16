package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionParser.KEYS;

public class SectionNode extends DirectiveNode {

  @Override
  public SectionArgumentsNode getArguments() {
    return (SectionArgumentsNode) super.getArguments();
  }

  public SectionNode() {
    super(AllDirectives.SECTION);
  }

  public String getName() {
    return getArguments().get(KEYS.NAME);
  }

  public int getMaxSize() {
    return Integer.parseInt(getArguments().get(KEYS.SIZE));
  }

  public String getNamespace() {
    return getArguments().get(KEYS.NAMESPACE);
  }

  public int getAlignment() {
     return Integer.parseInt(getArguments().get(KEYS.ALIGN));
  }

  public int getAppendTo() {
     return Integer.parseInt(getArguments().get(KEYS.APPEND_TO));
  }


  public SectionStatus getStatus() {
    return SectionStatus.valueOf(getArguments().get(KEYS.STATUS));
  }

  public boolean isAdvanceOrg() {
    return getArguments().get(KEYS.RETURNORG) == null;
  }

  public enum SectionStatus {FORCE, FREE, SUPERFREE, SEMIFREE, SEMISUBFREE, OVERWRITE, BANKHEADER}
}
