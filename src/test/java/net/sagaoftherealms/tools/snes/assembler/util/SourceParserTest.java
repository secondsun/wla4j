package net.sagaoftherealms.tools.snes.assembler.util;

import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DefinitionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.StructNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

public class SourceParserTest {

  @Test
  public void testAnonymousLabelNode() {
    fail(
        "This test will test that when - or + are used as labels the parser identifies them as such instead of as parts of a arthimetic operation");
  }

  @Test
  public void testShiftVsGetByteNode() {
    fail("This test will test >,<, >>,<< are handled as get byte and bit shift nodes");
  }

  /**
   * This test tests single directive tokens and makes sure that we can consume them.
   *
   * <p>Validation directives is another test.
   *
   * @param sourceLine the source code line
   * @param expectedDirective the expected directive sourceLine parses to.
   */
  @ParameterizedTest
  @CsvSource({"'.DBCOS 0.2, 10, 3.2, 120.0, 1.3', DBCOS, '[.2,10,3.2,120.0,1.3]'"})
  public void testParseDirectiveWithArgumentsToken(
      String sourceLine,
      String expectedDirective,
      @ConvertWith(DoubleArrayConverter.class) List<Double> arguments) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);
    var parser = new SourceParser(scanner);

    DirectiveNode node = (DirectiveNode) parser.nextNode();

    assertEquals(arguments.size(), node.getArguments().size());
  }

  @ParameterizedTest
  @CsvSource({".DBCOS 0.2"})
  public void testParsingDirectivesFailWithTooFewArgumentsToken(String sourceLine) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);
    var parser = new SourceParser(scanner);

    assertThrows(ParseException.class, () -> parser.nextNode());
  }

  @Test
  public void testLabelFailsIfOutputLibrary() {
    fail("See pass_1.c#788");
  }

  @Test
  public void testLabelFailsIfNoMemoryPosition() {
    fail("See pass_1.c#792");
  }

  @Test
  public void testLabelFailsIfInBankHeaderSection() {
    fail("See pass_1.c#802");
  }

  @Test
  public void testLabelInActiveMacro() {
    fail("See pass_1.c#807");
  }

  @Test
  public void testDecodeOtherArchOpcodeToken() {
    fail("This will deal with checking types and such on opcodes");
  }

  @Test
  public void testParseRamSectionToken() {
    fail(
        "This test should test that the ramsection directive starts a statement style block that respects ramsections."); // see pass_1.c#776
  }

  @Test
  public void firstStringTokenWithExpandedMacro() {
    fail("See pass_1.c#649");
  }

  @Test
  public void testParseEnumToken() {
    // see pass_1.c#776
    fail(
        "This test should test that the enum directive starts a statement style block that respects enums.");
  }

  @Test
  public void parseBasicEnum() {
    final String enumSource = ".ENUM $C000\n" + ".ENDE";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    EnumNode enumNode = (EnumNode) parser.nextNode();

    assertEquals(NodeTypes.ENUM, enumNode.getType());
    assertEquals("49152", enumNode.getAddress());
  }

  @Test
  public void parseBasicEnumBody() {
    final String enumSource =
        ".ENUM $C000\n"
            + " SEASON_SPRING db\n"
            + "SEASON_SUMMER BYTE\n"
            + "SEASON_SUMMER_2 dw\n"
            + "SEASON_FALL DS 16\n"
            + "SEASON_WINTER dsW 16\n"
            + ".ENDE";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    var enumNode = (EnumNode) parser.nextNode();

    var body = enumNode.getBody();
    assertEquals(5, body.getChildren().size());
    assertEquals(1, ((DefinitionNode) body.getChildren().get(0)).getSize());
    assertEquals(1, ((DefinitionNode) body.getChildren().get(1)).getSize());
    assertEquals(2, ((DefinitionNode) body.getChildren().get(2)).getSize());
    assertEquals(16, ((DefinitionNode) body.getChildren().get(3)).getSize());
    assertEquals(32, ((DefinitionNode) body.getChildren().get(4)).getSize());
    assertEquals("SEASON_SPRING", ((DefinitionNode) body.getChildren().get(0)).getLabel());
    assertEquals("SEASON_SUMMER", ((DefinitionNode) body.getChildren().get(1)).getLabel());
  }

  @ParameterizedTest
  @CsvSource({
    ".IF 5 > 10",
    ".IFDEF LABEL",
    ".IFDEFM \\5",
    ".IFEQ 4 4", // Two constant expressions
    ".IFEQ 4 * 4 BERRIES", // A math experssion and a label
    ".IFEXISTS \"FileName String\"",
    ".IFGR 4 * 4 BERRIES",
    ".IFGR 4 4 ",
    ".IFGREQ 4 * 4 BERRIES",
    ".IFGREQ 4 BERRIES",
    ".IFLE BERRIES 45",
    ".IFLEEQ BERRIES @JAMMING",
    ".IFNDEF LABEL",
    ".IFNDEFM \\5",
    ".IFNEQ BERRIES :JAMMING",
  })
  public void parseIfs(String ifStatement) {
    var source =
        ifStatement + "\n .db 1, \"Two\", 3 \n" + " .else \n " + ".db 42.0,  5, \"Six\"\n" + ".endif";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    var ifNode = (DirectiveNode) parser.nextNode();
    var thenNode = (DirectiveBodyNode) ifNode.getBody().getChildren().get(0);
    var elseNode = (DirectiveBodyNode) ifNode.getBody().getChildren().get(1);

    assertEquals("Two", ((DirectiveNode) thenNode.getChildren().get(0)).getArguments().get(1));
    assertEquals("5", ((DirectiveNode) elseNode.getChildren().get(0)).getArguments().get(1));
  }

  @Test
  public void parseEnumBodyWithIfDirective() {
    // TODO Include all the types of IFs as parameterized test.
    fail("See pass_1.c#1137");
  }

  @Test
  public void parseStructWithEmbeddedIfDirective() {
    fail("See pass_1 Line 2446");
  }

  @Test
  public void parseStruct() {
    var source = ".STRUCT mon\n" + "name ds 2\n" + "age  db\n" + ".ENDST";

    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    StructNode structNode = (StructNode) parser.nextNode();

    var body = structNode.getBody();

    assertEquals(2, body.getChildren().size());
    assertEquals(2, ((DefinitionNode) body.getChildren().get(0)).getSize());
    assertEquals(1, ((DefinitionNode) body.getChildren().get(1)).getSize());
    assertEquals("name", ((DefinitionNode) body.getChildren().get(0)).getLabel());
    assertEquals("age", ((DefinitionNode) body.getChildren().get(1)).getLabel());
    assertEquals("mon", structNode.getName());
  }

  @Test
  public void parseEnumBodyWithStructDefined() {
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

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    var structNode = (StructNode) parser.nextNode();
    var structBody = structNode.getBody();
    var enumNode = (EnumNode) parser.nextNode();
    var enumBody = enumNode.getBody();

    assertEquals("mon", structNode.getName());
    assertEquals(0xA000, Integer.parseInt(enumNode.getAddress()));
    assertEquals(9, enumBody.getChildren().size());
    assertEquals(1, ((DefinitionNode) enumBody.getChildren().get(0)).getSize());
    assertEquals(2, ((DefinitionNode) enumBody.getChildren().get(2)).getSize());

    assertEquals("name", ((DefinitionNode) structBody.getChildren().get(0)).getLabel());
    assertEquals("age", ((DefinitionNode) structBody.getChildren().get(1)).getLabel());
    assertTrue(((DefinitionNode) structBody.getChildren().get(1)).getStructName().isEmpty());
    assertEquals("mon", structNode.getName());
    assertEquals(3, ((DefinitionNode) enumBody.getChildren().get(7)).getSize());
    assertEquals("monster", ((DefinitionNode) enumBody.getChildren().get(7)).getLabel());
    assertEquals("mon", ((DefinitionNode) enumBody.getChildren().get(7)).getStructName().get());
    assertEquals(1, ((DefinitionNode) enumBody.getChildren().get(8)).getSize());
    assertEquals("dragon", ((DefinitionNode) enumBody.getChildren().get(8)).getLabel());
    assertEquals("mon", ((DefinitionNode) enumBody.getChildren().get(8)).getStructName().get());
  }

  /** Only if directives are allowed inside of a DirectiveBody */
  @Test
  public void parseEnumBodyWithDirectiveThrowsParseException() {
    final String enumSource =
        ".ENUM $C000\n"
            + " SEASON_SPRING db\n"
            + "SEASON_SUMMER BYTE\n"
            + "SEASON_SUMMER_2 dw\n"
            + "SEASON_FALL DS 16\n"
            + "SEASON_WINTER dsW 16\n"
            + ".PRINTT 'error'\n"
            + ".ENDE";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);

    assertThrows((ParseException.class), () -> parser.nextNode());
  }

  @Test
  public void exceptionIfNoEnde() {

    final String enumSource = ".ENUM $C000\n";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    Assertions.assertThrows(ParseException.class, () -> parser.nextNode());
  }
}
