package net.sagaoftherealms.tools.snes.assembler.main;

import java.io.Serializable;
import java.util.Objects;

public final class ArchRoot implements Serializable {
  private final String path;
  private final String arch;

  public ArchRoot(String path, String arch) {
    this.path = path;
    this.arch = arch;
  }

  public String getPath() {
    return path;
  }

  public String getArch() {
    return arch;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchRoot archRoot = (ArchRoot) o;
    return Objects.equals(path, archRoot.path) && Objects.equals(arch, archRoot.arch);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, arch);
  }
}
