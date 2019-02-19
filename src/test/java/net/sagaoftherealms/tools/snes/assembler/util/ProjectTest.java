package net.sagaoftherealms.tools.snes.assembler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CompletableFuture;
import net.sagaoftherealms.tools.snes.assembler.main.Project;
import org.junit.jupiter.api.Test;

public class ProjectTest {

  @Test
  public void testParseGBCProject() {
    Project gbcTestProject = new Project.Builder("src/test/resources/projects/gbc").build();

    assertEquals("gb", gbcTestProject.getMainArch());
    assertEquals("main.s", gbcTestProject.getMain());

//    fail("Not implemented");

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

  @Test
  /**
   * SNE projects have at least 65816 and SPC700 code in them
   */
  public void testParseSNESProject() {
    fail("Not implemented");
  }

}
