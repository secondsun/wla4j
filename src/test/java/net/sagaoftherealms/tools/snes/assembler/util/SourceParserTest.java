package net.sagaoftherealms.tools.snes.assembler.util;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.BANK;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.NAME;
import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCodeZ80;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.Opcodes65816;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.LabelNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.Node;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.NodeTypes;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.OpcodeNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.ExpressionParser;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.NumericExpressionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode.SectionStatus;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

public class SourceParserTest {

  @Test
  public void testExpressionParser() {
    var sourceLine = "NUM_SEED_TREES*8";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);
    var parser = new SourceParser(scanner);

    var expressionNode = ExpressionParser.expressionNode(parser);

    assertEquals(NodeTypes.LABEL, expressionNode.getChildren().get(0).getType());
    assertEquals(NodeTypes.NUMERIC_CONSTANT, expressionNode.getChildren().get(1).getType());
    assertEquals(TokenTypes.MULTIPLY, ((NumericExpressionNode) expressionNode).getOperationType());
  }

  @ParameterizedTest
  @CsvSource({
    "'- rti \n jmp -'", // Label, opcode newline opcode
    "'--- rti \n jmp ---'", // Label, opcode newline opcode
    "'+ rti \n jmp +'", // Label, opcode newline opcode
    "'++ rti \n jmp ++'", // Label, opcode newline opcode
    "'+++ rti \n jmp +++'", // Label, opcode newline opcode
    "'-- rti \n jmp --'", // Label, opcode newline opcode
    "'__ rti \n jmp _f'", // Label, opcode newline opcode
    "'__ rti \n jmp _b'" // Label, opcode newline opcode
  })
  public void testAnonymousLabelNode(String sourceLine) {
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(sourceLine), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);
    var parser = new SourceParser(scanner);

    LabelNode node = (LabelNode) parser.nextNode();
    OpcodeNode rti = (OpcodeNode) parser.nextNode();
    OpcodeNode jmp = (OpcodeNode) parser.nextNode();

    assertNotNull(node);
    assertNotNull(rti);
    assertNotNull(jmp);
    assertNotNull(node.getLabelName());
    assertEquals(0, rti.getChildren().size()); // RTI has no arguments
    assertEquals(1, jmp.getChildren().size()); // JMP has one argument
    assertEquals(sourceLine.split("\\s")[0], node.getLabelName());
    assertEquals(NodeTypes.OPCODE_ARGUMENT, jmp.getChildren().get(0).getType());
  }

  @Test
  public void testShiftVsGetByteNode() {
    fail("This test will test >,<, >>,<< are handled as getString byte and bit shift nodes");
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
  public void testLabelFailsIfInBankHeaderSection() {
    final String enumSource =
        ".SECTION \"BANKHEADER\" SEMIFREE\n"
            + "\n"
            + "EmptyHandler:\n"
            + "       rti\n"
            + "\n"
            + ".ENDS";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);

    assertThrows(
        ParseException.class,
        () -> {
          parser.nextNode();
        });
  }

  @Test
  public void testParseRamSectionToken() {
    // Source for source : ages-disasm wram.s#2548
    var source =
        ".RAMSECTION \"RAM 2\" BANK 2 SLOT 3\n"
            + "\n"
            + "; $d000 used as part of the routine for redrawing the collapsed d2 cave in the present\n"
            + "w2Filler1:\t\t\tdsb $0800\n"
            + "\n"
            + "; This is a list of values for scrollX or scrollY registers to make the screen turn all\n"
            + "; wavy (ie. in underwater areas).\n"
            + "w2WaveScrollValues:\t\tdsb $80\t; $d800/$d800\n"
            + "\n"
            + "w2Filler7:\t\t\tdsb $80\n"
            + "\n"
            + "; Tree refill data also used for child and an event in room $2f7\n"
            + "w2SeedTreeRefillData:\t\tdsb NUM_SEED_TREES*8 ; $d900/3:dfc0\n"
            + "\n"
            + ".ifdef ROM_SEASONS\n"
            + "w2Filler9:\t\t\tdsb $40\n"
            + ".endif\n"
            + "\n"
            + "; Bitset of positions where objects (mostly npcs) are residing. When one of these bits is\n"
            + "; set, this will prevent Link from time-warping onto the corresponding tile.\n"
            + "w2SolidObjectPositions:\t\t\tdsb $010 ; $d980\n"
            + "\n"
            + "w2Filler6:\t\t\tdsb $70\n"
            + "\n"
            + "; Used as the \"source\" palette when fading between two sets of palettes\n"
            + "w2ColorComponentBuffer1:\tdsb $090 ; $da00\n"
            + "\n"
            + "; Keeps a history of Link's path when objects (Impa) are following Link.\n"
            + "; Consists of 16 entries of 3 bytes each: direction, Y position, X position.\n"
            + "w2LinkWalkPath:\t\t\tdsb $030 ; $da90\n"
            + "\n"
            + "w2ChangedTileQueue:\t\tdsb $040 ; $dac0\n"
            + "\n"
            + "; Used as the \"destination\" palette when fading between two sets of palettes\n"
            + "w2ColorComponentBuffer2:\tdsb $090 ; $db00\n"
            + "\n"
            + "w2AnimationQueue:\t\tdsb $20\t; $db90\n"
            + "\n"
            + "w2Filler4:\t\t\tdsb $50\n"
            + "\n"
            + "; Each $40 bytes is one floor\n"
            + "w2DungeonLayout:\tdsb $100\t; $dc00\n"
            + "\n"
            + "w2Filler2: dsb $100\n"
            + "\n"
            + "w2GbaModePaletteData:\tdsb $80\t\t; $de00\n"
            + "\n"
            + "; The \"base\" palettes on a screen.\n"
            + "w2AreaBgPalettes:\tdsb $40\t\t; $de80\n"
            + "w2AreaSprPalettes:\tdsb $40\t\t; $dec0\n"
            + "\n"
            + "; The palettes that are copied over during vblank\n"
            + "w2BgPalettesBuffer:\tdsb $40\t\t; $df00\n"
            + "w2SprPalettesBuffer:\tdsb $40\t\t; $df40\n"
            + "\n"
            + "; The \"base\" palettes have \"fading\" operations applied, and the result is written here.\n"
            + "w2FadingBgPalettes:\tdsb $40\t\t; $df80\n"
            + "w2FadingSprPalettes:\tdsb $40\t\t; $dfc0\n"
            + "\n"
            + ".ENDS";

    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);

    var node = parser.nextNode();

    assertTrue(node instanceof DirectiveNode);
    var directiveNode = (DirectiveNode) node;
    var arguments = (RamsectionArgumentsNode) directiveNode.getArguments();

    assertEquals("RAM 2", arguments.get(NAME));
    assertEquals("2", arguments.get(BANK));

    var body = directiveNode.getBody();
    DefinitionNode randomLabel = (DefinitionNode) body.getChildren().get(3);
    assertEquals("w2SeedTreeRefillData", randomLabel.getLabel());

    DirectiveNode ifNode = (DirectiveNode) body.getChildren().get(4);
    assertEquals("IFDEF", ifNode.getDirectiveType().getName());
    randomLabel =
        (DefinitionNode) body.getChildren().get(5); // test that we are getting the right type
  }

  @Test
  public void firstStringTokenWithExpandedMacro() {
    fail("See pass_1.c#649");
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
    assertEquals(1, (int) ((DefinitionNode) body.getChildren().get(0)).getSize().evaluate());
    assertEquals(1, (int) ((DefinitionNode) body.getChildren().get(1)).getSize().evaluate());
    assertEquals(2, (int) ((DefinitionNode) body.getChildren().get(2)).getSize().evaluate());
    assertEquals(16, (int) ((DefinitionNode) body.getChildren().get(3)).getSize().evaluate());
    assertEquals(32, (int) ((DefinitionNode) body.getChildren().get(4)).getSize().evaluate());
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
        ifStatement
            + "\n .db 1, \"Two\", 3 \n"
            + " .else \n "
            + ".db 42.0,  5, \"Six\"\n"
            + ".endif";
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

    assertEquals(
        "Two", ((DirectiveNode) thenNode.getChildren().get(0)).getArguments().getString(1));
    assertEquals("5", ((DirectiveNode) elseNode.getChildren().get(0)).getArguments().getString(1));
  }

  @Test
  public void testDefineByteParser() {
    fail("");
  }

  @Test
  public void testDefineWordParser() {
    fail("");
  }

  @Test
  public void testDefineByteSeriesParser() {
    fail("");
  }

  @Test
  public void testDefineWordSeriesParser() {
    fail("");
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
  public void parseEnumBodyWithIfDirective() {
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
            + ".IFDEF THREE\n"
            + "   monster   INSTANCEOF mon 3 ; three instances of structure mon\n"
            + ".IFDEF THREE\n"
            + "   monster   INSTANCEOF mon 3 ; three instances of structure mon\n"
            + ".ELSE\n"
            +
            // 7 = monster 8 = monster.name 12 = monster.1.age 17 = monster.3.name
            "     dragon    INSTANCEOF mon   ; one mon\n"
            + ".ENDIF\n"
            + ".ELSE\n"
            +
            // 7 = monster 8 = monster.name 12 = monster.1.age 17 = monster.3.name
            "     dragon    INSTANCEOF mon   ; one mon\n"
            + ".ENDIF\n"
            + // 21 dragon.age
            ".ENDE";

    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    parser.nextNode();
    var enumNode = (EnumNode) parser.nextNode();

    assertEquals(
        AllDirectives.IFDEF,
        ((DirectiveNode) (enumNode.getBody().getChildren().get(7))).getDirectiveType());
    assertEquals(
        "monster",
        ((DefinitionNode)
                ((IfBodyNode) ((DirectiveNode) (enumNode.getBody().getChildren().get(7))).getBody())
                    .getThenBody()
                    .getChildren()
                    .get(0))
            .getLabel());
    assertEquals(
        "dragon",
        ((DefinitionNode)
                ((IfBodyNode) ((DirectiveNode) (enumNode.getBody().getChildren().get(7))).getBody())
                    .getElseBody()
                    .getChildren()
                    .get(0))
            .getLabel());
    assertEquals(
        AllDirectives.IFDEF,
        ((DirectiveNode)
                ((IfBodyNode) ((DirectiveNode) (enumNode.getBody().getChildren().get(7))).getBody())
                    .getThenBody()
                    .getChildren()
                    .get(1))
            .getDirectiveType());
  }

  @Test
  /**
   * DB includes strings, DW does not.
   * The test sources have DB with string, but I want to enforce the failure case.
   */
  public void testDWFailsWithString() {
    String source = ".dw \"Fail\"";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    assertThrows(ParseException.class, ()->parser.nextNode());
  }

  @Test
  public void parseStructWithEmbeddedIfDirective() {
    var source =
        ".STRUCT mon                ; check out the documentation on\n"
            + ".IFDEF THREE\n"
            + "name ds 2                  ; .STRUCT\n"
            + ".ELSE\n"
            + "age  db\n"
            + ".ENDIF\n"
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
            + "   monster   INSTANCEOF mon 3 ; three instances of structure mon\n"
            + "     dragon    INSTANCEOF mon   ; one mon\n"
            + ".ENDE";

    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(source), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);
    StructNode structNode = (StructNode) parser.nextNode();

    assertEquals(
        AllDirectives.IFDEF,
        ((DirectiveNode) (structNode.getBody().getChildren().get(0))).getDirectiveType());
    assertEquals(
        "age",
        ((DefinitionNode)
                ((DirectiveBodyNode)
                        ((IfBodyNode)
                                ((DirectiveNode) (structNode.getBody().getChildren().get(0)))
                                    .getBody())
                            .getElseBody())
                    .getChildren()
                    .get(0))
            .getLabel());
    assertEquals(
        2,
        (int)
            ((DefinitionNode)
                    ((DirectiveBodyNode)
                            ((IfBodyNode)
                                    ((DirectiveNode) (structNode.getBody().getChildren().get(0)))
                                        .getBody())
                                .getThenBody())
                        .getChildren()
                        .get(0))
                .getSize()
                .evaluate());
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
    assertEquals(2, (int) ((DefinitionNode) body.getChildren().get(0)).getSize().evaluate());
    assertEquals(1, (int) ((DefinitionNode) body.getChildren().get(1)).getSize().evaluate());
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
    assertEquals(1, (int) ((DefinitionNode) enumBody.getChildren().get(0)).getSize().evaluate());
    assertEquals(2, (int) ((DefinitionNode) enumBody.getChildren().get(2)).getSize().evaluate());

    assertEquals("name", ((DefinitionNode) structBody.getChildren().get(0)).getLabel());
    assertEquals("age", ((DefinitionNode) structBody.getChildren().get(1)).getLabel());
    assertTrue(((DefinitionNode) structBody.getChildren().get(1)).getStructName().isEmpty());
    assertEquals("mon", structNode.getName());
    assertEquals(3, (int) ((DefinitionNode) enumBody.getChildren().get(7)).getSize().evaluate());
    assertEquals("monster", ((DefinitionNode) enumBody.getChildren().get(7)).getLabel());
    assertEquals("mon", ((DefinitionNode) enumBody.getChildren().get(7)).getStructName().get());
    assertEquals(1, (int) ((DefinitionNode) enumBody.getChildren().get(8)).getSize().evaluate());
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
            + ".PRINTT \"error\"\n"
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

  /**
   * Sections can have a lot of permuations of type, size, etc. See the section Node for the stuff I
   * will need to write.
   */
  @Test
  public void testSectionBasic() {
    final String enumSource =
        ".SECTION \"EmptyVectors\" SEMIFREE\n"
            + "\n"
            + "EmptyHandler:\n"
            + "       rti\n"
            + "\n"
            + ".ENDS\n"
            + ".8BIT\n";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(Opcodes65816.opt_table);

    SourceParser parser = new SourceParser(scanner);

    assertTimeout(
        Duration.ofSeconds(1),
        () -> {
          SectionNode node = (SectionNode) parser.nextNode();
          assertEquals(AllDirectives.SECTION, ((DirectiveNode) node).getDirectiveType());
          assertEquals("EmptyVectors", node.getName());
          assertEquals(SectionStatus.SEMIFREE, node.getStatus());

          Node emptyHandlerLabelNode = node.getBody().getChildren().get(0);
          assertEquals(NodeTypes.LABEL, emptyHandlerLabelNode.getType());
          Node rtiOpLabel = node.getBody().getChildren().get(1);
          assertEquals(NodeTypes.OPCODE, rtiOpLabel.getType());
          DirectiveNode eightBit = (DirectiveNode) parser.nextNode();
          assertNotNull(eightBit);
        });
  }

  /** macro_1 is a basic macro with no variables or lookups or anything. */
  @Test
  public void testDefineMacro1BasicMacro() throws IOException {
    final String macroSource =
        IOUtils.toString(
            SourceParserTest.class
                .getClassLoader()
                .getResourceAsStream("parseMacro/define-macro-1.s"),
            "UTF-8");
    final String outfile = "define_macro_1.out";
    final String inputFile = "define_macro_1.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.OPCODES);

    SourceParser parser = new SourceParser(scanner);

    MacroNode node = (MacroNode) parser.nextNode();
    assertEquals("wait_1s", node.getName());

    var body = node.getBody();
    assertEquals(NodeTypes.OPCODE, body.getChildren().get(0).getType());
    assertEquals(NodeTypes.LABEL, body.getChildren().get(1).getType());
    assertEquals("-", ((LabelNode) body.getChildren().get(1)).getLabelName());
    assertEquals(NodeTypes.OPCODE, body.getChildren().get(2).getType());
  }

  /** macro_2 is a basic macro with two variables */
  @Test
  public void testDefineMacro2DeclaredVariables() throws IOException {
    final String macroSource =
        IOUtils.toString(
            SourceParserTest.class
                .getClassLoader()
                .getResourceAsStream("parseMacro/define_macro_2.s"),
            "UTF-8");
    final String outfile = "define_macro_2.out";
    final String inputFile = "parseMacro/define_macro_2.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.OPCODES);

    SourceParser parser = new SourceParser(scanner);

    MacroNode node = (MacroNode) parser.nextNode();
    assertEquals("Engine_FillMemory", node.getName());

    var arguments = node.getArguments();
    assertEquals("value", arguments.getString(1));
    assertEquals("value2", arguments.getString(2));
    assertEquals("value3", arguments.getString(3));
  }

  /** macro_3 is a basic macro with labels inside that refer to macro arguments by number */
  @Test
  public void testDefineMacro3DeclaredVariables() throws IOException {
    final String macroSource =
        IOUtils.toString(
            SourceParserTest.class
                .getClassLoader()
                .getResourceAsStream("parseMacro/define_macro_3.s"),
            "UTF-8");
    final String outfile = "define_macro_3.out";
    final String inputFile = "parseMacro/define_macro_3.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.OPCODES);

    SourceParser parser = new SourceParser(scanner);

    MacroNode node = (MacroNode) parser.nextNode();

    var arguments = node.getArguments();
    assertEquals(1, arguments.size());
    var body = node.getBody();
    DirectiveNode dbNode = (DirectiveNode) body.getChildren().get(0);
    var dbArgs = dbNode.getArguments();
    assertEquals(2, dbArgs.size());
    assertEquals(
        "\\1",
        ((LabelNode) ((NumericExpressionNode) dbArgs.getChildren().get(1)).getChildren().get(0))
            .getLabelName());
  }

  /** macro_3 is a basic macro with labels inside that refer to macro arguments by number */
  @Test
  public void testLargeFile() throws IOException {
    final String macroSource =
        IOUtils.toString(
            SourceParserTest.class
                .getClassLoader()
                .getResourceAsStream("parseLargeFiles/script_commands.s"),
            "UTF-8");
    final String outfile = "script_commands.out";
    final String inputFile = "parseLargeFiles/script_commands.s";
    final int lineNumber = 0;

    var data = new InputData(new Flags(outfile));
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.OPCODES);

    SourceParser parser = new SourceParser(scanner);

    var node = parser.nextNode();

    while (node != null) {
      System.out.println(node);
      node = parser.nextNode();
    }
  }
}
