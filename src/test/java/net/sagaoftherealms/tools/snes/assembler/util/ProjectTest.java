package net.sagaoftherealms.tools.snes.assembler.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sagaoftherealms.tools.snes.assembler.main.ArchRoot;
import net.sagaoftherealms.tools.snes.assembler.main.Project;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import org.junit.jupiter.api.Test;

public class ProjectTest {

  @Test
  public void testParseGBCProject() {
    Project gbcTestProject = new Project.Builder("src/test/resources/projects/gbc").build();

    assertEquals("gb", gbcTestProject.getRetro().getMainArch());
    assertEquals("main.s", gbcTestProject.getRetro().getMain());
  }

  @Test
  /** SNE projects have at least 65816 and SPC700 code in them */
  public void testParseSNESProject() {

    Project snesTestProject = new Project.Builder("src/test/resources/projects/snes").build();

    assertEquals("65816", snesTestProject.getRetro().getMainArch());
    assertEquals("main.s", snesTestProject.getRetro().getMain());

    assertEquals(2, snesTestProject.getRetro().getArchRoots().size());
    assertTrue(snesTestProject.getRetro().getArchRoots().contains(new ArchRoot("spc", "spc700")));
    assertTrue(snesTestProject.getRetro().getArchRoots().contains(new ArchRoot("gsu", "sfx")));
  }

  @Test
  /** Use retro.json to parse ages-disasm */
  public void parseAgesDisasmWithRetroJson() throws InterruptedException {
    // Thread.sleep(10000);
    var includedFile = "objects/macros.s";

    Project agesProject = new Project.Builder("src/test/resources/ages-disasm").build();

    var response = agesProject.getParseTree(includedFile);

    Logger.getAnonymousLogger().log(Level.INFO, "Safe to heapdump");

    // Thread.sleep(10000);

    assertNotNull(response);

    assertEquals(
        "obj_Conditional",
        ((MacroNode) ((List<Node>) agesProject.getParseTree(includedFile)).get(1)).getName());
  }

  @Test
  /** Use retro.json to parse 6-fpstest */
  public void parse60FPSTest() throws InterruptedException {
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

  /*
   *
   * Things to test  :
   * Important items are stored in memory like definitions and their location
   *  Definitions are cached
   * */

  /* Old test
    *@Test
  @Disabled
  public void multiFileTest() throws IOException {
    var sourceDirectory = "ages-disasm";
    var sourceRoot = "main.s";
    var includedFile = "ages-disasm/objects/macros.s";
    MultiFileParser multiParser = new MultiFileParser(OpCodeZ80.opcodes());
    multiParser.parse(sourceDirectory, sourceRoot);
    assertNotNull(multiParser.getNodes(includedFile));
    assertEquals(
        "obj_Conditional",
        ((MacroNode) ((List<Node>) multiParser.getNodes(includedFile)).get(1)).getName());
  }


     */

}