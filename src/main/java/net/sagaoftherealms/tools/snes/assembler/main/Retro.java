package net.sagaoftherealms.tools.snes.assembler.main;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class Retro implements Serializable {
  private final String main, mainArch;
  private final Set<ArchRoot> archRoots = new HashSet<>();

  public Retro(String main, String mainArch) {
    this.main = main;
    this.mainArch = mainArch;
  }

  public String getMain() {
    return main;
  }

  public String getMainArch() {
    return mainArch;
  }

  public Set<ArchRoot> getArchRoots() {
    return Collections.unmodifiableSet(archRoots);
  }

  /**
   * This is a factory method that creates a retro from a json file
   *
   * @param retro a json object that represents a retro.json file
   * @return a object of a retro.json file
   */
  public static Retro fromJson(JsonObject retro) {
    String main = retro.getString("main");
    String mainArch = retro.getString("main-arch");
    Retro retroObject = new Retro(main, mainArch);

    if (retro.containsKey("arch-roots")) {
      JsonArray archRoots = retro.getJsonArray("arch-roots");
      archRoots.forEach(
          root -> {
            String path = root.asJsonObject().getString("path");
            String arch = root.asJsonObject().getString("arch");
            ArchRoot archRoot = new ArchRoot(path, arch);
            retroObject.archRoots.add(archRoot);
          });
    }

    return retroObject;
  }
}
