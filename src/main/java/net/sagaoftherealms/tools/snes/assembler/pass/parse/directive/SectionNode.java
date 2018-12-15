package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;

public class SectionNode extends DirectiveNode {

  private String name;
  private int maxSize;
  private String namespace;
  private int alignment;
  private SectionStatus status = SectionStatus.FREE;//FREE is default
  private boolean advanceOrg = true;

  public SectionNode() {
    super(AllDirectives.SECTION);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public int getAlignment() {
    return alignment;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

  public SectionStatus getStatus() {
    return status;
  }

  public void setStatus(
      SectionStatus status) {
    this.status = status;
  }

  public boolean isAdvanceOrg() {
    return advanceOrg;
  }

  public void setAdvanceOrg(boolean advanceOrg) {
    this.advanceOrg = advanceOrg;
  }

  public enum SectionStatus {FORCE, FREE, SUPERFREE, SEMIFREE, SEMISUBFREE, OVERWRITE, BANKHEADER}
}
