package net.sagaoftherealms.tools.snes.assembler.util.analyzer;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.toStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode65816;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructNode;
import org.junit.jupiter.api.Test;

public class SourceAnalyzerTest {

  @Test
  public void testStructDefinesSizeOf() {
    fail(
        "Struct should define a __sideof__.  See https://wla-dx.readthedocs.io/en/latest/asmdiv.html#struct-enemy-object");
  }

  @Test
  public void handleAsciiCommands() {
    fail(
        "\n"
            + "ASCII commands work with .DB and .ASC strings. They are as follows:\n"
            + "'\\0' -> insert null byte\n"
            + "'\\x' -> insert hex character\n"
            + "'\\>' -> set highest bit (0x80) of preceding character\n"
            + "'\\<' -> set highest bit (0x80) of proceeding character\n"
            + "\n"
            + ".ASC hex characters are NOT remapped. This is useful if you need to write special characters. Example:\n"
            + ".ASC \"My special character: \"\n"
            + ".DB $59\n"
            + "...becomes\n"
            + ".ASC \"My special character: \\x59\"\n"
            + "=============================\n"
            + ".DL for 65816 works just like you'd expect it. It write the bank byte when used with labels. :D");
  }

  @Test
  public void testLabelFailsIfOutputLibrary() {
    fail("See pass_1.c#788");
  }

  @Test
  public void testLabelFailsIfNoMemoryPosition() {
    // Org or orga have to be set before labels can be defined.
    fail("See pass_1.c#792");
  }

  @Test
  public void testBankHeaderSection() {
    fail(
        "testing BANKHEADER see https://wla-dx.readthedocs.io/en/latest/asmdiv.html?highlight=BANKHEADER");
  }

  @Test
  public void testFilenameExsists() {
    fail("This is testing a filename string (IE .IFEXISTS ) exists");
  }

  public void testExpandingEnumStructDeclarationToDefinedStruct() {
    var source =
        ".STRUCT mon                ; check out the documentation on\n"
            + "name ds 2                  ; .STRUCT\n"
            + "age  db\n"
            + ".ENDST\n"
            + "\n"
            + ".ENUM $A000\n"
            + "_scroll_x DB               ; db  - define byte (byt and byte work also)\n"
            + "_scroll_y DB\n"
            + "player_x: DW               ; dw  - define word (word works also)\n"
            + "player_y: DW\n"
            + "map_01:   DS  16           ; ds  - define size (bytes)\n"
            + "map_02    DSB 16           ; dsb - define size (bytes)\n"
            + "map_03    DSW  8           ; dsw - define size (words)\n"
            + "monster   INSTANCEOF mon 3 ; three instances of structure mon\n"
            +
            // 7 = monster 8 = monster.name 12 = monster.1.age 17 = monster.3.name
            "dragon    INSTANCEOF mon   ; one mon\n"
            + // 21 dragon.age
            ".ENDE";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile(toStream(source), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    SourceParser parser = new SourceParser(scanner);
    var structNode = (StructNode) parser.nextNode();
    var enumNode = (EnumNode) parser.nextNode();
    var enumBody = enumNode.getBody();

    assertEquals("mon", structNode.getName());
    assertEquals(0xA000, enumNode.getAddress());
    assertEquals(22, enumBody.getChildren().size());
    assertEquals(1, ((DefinitionNode) enumBody.getChildren().get(0)).getSize());
    assertEquals(2, ((DefinitionNode) enumBody.getChildren().get(2)).getSize());

    assertEquals("name", ((DefinitionNode) enumBody.getChildren().get(0)).getLabel());
    assertEquals("age", ((DefinitionNode) enumBody.getChildren().get(1)).getLabel());
    assertEquals("mon", structNode.getName());
    assertEquals(3, ((DefinitionNode) enumBody.getChildren().get(7)).getSize());
    assertEquals("monster", ((DefinitionNode) enumBody.getChildren().get(7)).getLabel());

    assertEquals(3, ((DefinitionNode) enumBody.getChildren().get(7)).getSize());
    assertEquals("monster", ((DefinitionNode) enumBody.getChildren().get(7)).getLabel());

    assertEquals(2, ((DefinitionNode) enumBody.getChildren().get(8)).getSize());
    assertEquals("monster.name", ((DefinitionNode) enumBody.getChildren().get(8)).getLabel());

    assertEquals(1, ((DefinitionNode) enumBody.getChildren().get(12)).getSize());
    assertEquals("monster.1.age", ((DefinitionNode) enumBody.getChildren().get(12)).getLabel());

    assertEquals(2, ((DefinitionNode) enumBody.getChildren().get(17)).getSize());
    assertEquals("monster.3.age", ((DefinitionNode) enumBody.getChildren().get(17)).getLabel());

    assertEquals(1, ((DefinitionNode) enumBody.getChildren().get(21)).getSize());
    assertEquals("dragon.age", ((DefinitionNode) enumBody.getChildren().get(21)).getLabel());

    fail(
        "See above.  During analysis monster and dragon should be expanded to make the previous pass");
  }
}
