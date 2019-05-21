package net.sagaoftherealms.tools.snes.assembler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sagaoftherealms.tools.snes.assembler.main.Project;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import org.junit.jupiter.api.Test;

/**
 * WLA-DX does not include support for assembling SuperFX chip macros. This class is testing support
 * for such things as I add them.
 */
public class SuperFxChipTest {

  @Test
  public void loadSuperFxTestProject() {
    var includedFile = "InitSNES.asm";
    Project superfxProject = new Project.Builder("src/test/resources/superfx/60fpxtest").build();

    var response = superfxProject.getParseTree(includedFile);

    Logger.getAnonymousLogger().log(Level.INFO, "Safe to heapdump");

    // Thread.sleep(10000);

    assertNotNull(response);

    assertEquals(
        "InitializeSNES",
        ((MacroNode) ((List<Node>) superfxProject.getParseTree(includedFile)).get(0)).getName());
  }
}
