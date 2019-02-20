package net.sagaoftherealms.tools.snes.assembler.util;


import static org.junit.jupiter.api.Assertions.assertEquals;

import net.sagaoftherealms.tools.snes.assembler.main.ArchRoot;
import net.sagaoftherealms.tools.snes.assembler.main.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProjectTest {

  @Test
  public void testParseGBCProject() {
    Project gbcTestProject = new Project.Builder("src/test/resources/projects/gbc").build();

    assertEquals("gb", gbcTestProject.getRetro().getMainArch());
    assertEquals("main.s", gbcTestProject.getRetro().getMain());

  }


  @Test
  /**
   * SNE projects have at least 65816 and SPC700 code in them
   */
  public void testParseSNESProject() {

    Project snesTestProject = new Project.Builder("src/test/resources/projects/snes").build();
    
    assertEquals("65816", snesTestProject.getRetro().getMainArch());
    assertEquals("main.s", snesTestProject.getRetro().getMain());
    
    assertEquals( 2, snesTestProject.getRetro().getArchRoots().size());
    Assertions.assertTrue(snesTestProject.getRetro().getArchRoots().contains(new ArchRoot("spc", "spc700")));
    Assertions.assertTrue(snesTestProject.getRetro().getArchRoots().contains(new ArchRoot("gsu", "sfx")));
    
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
