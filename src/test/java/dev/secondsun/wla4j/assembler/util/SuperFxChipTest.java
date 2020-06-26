package dev.secondsun.wla4j.assembler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.secondsun.wla4j.assembler.main.Project;
import dev.secondsun.wla4j.assembler.pass.parse.Node;
import dev.secondsun.wla4j.assembler.pass.parse.directive.macro.MacroNode;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * WLA-DX does not include support for assembling SuperFX chip macros. This class is testing support
 * for such things as I add them.
 */
@Disabled
public class SuperFxChipTest {

  @Test
  public void loadSuperFxTestProject() {
    var includedFile = "InitSNES.asm";
    Project superfxProject =
        new Project.Builder(new File("src/test/resources/superfx/60fpxtest").toURI()).build();

    var response = superfxProject.getParseTree(includedFile);

    Logger.getAnonymousLogger().log(Level.INFO, "Safe to heapdump");

    // Thread.sleep(10000);

    assertNotNull(response);

    assertEquals(
        "InitializeSNES",
        ((MacroNode) ((List<Node>) superfxProject.getParseTree(includedFile)).get(0)).getName());
  }
}
