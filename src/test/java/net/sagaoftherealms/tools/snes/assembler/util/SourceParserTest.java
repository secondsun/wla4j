package net.sagaoftherealms.tools.snes.assembler.util;

import net.sagaoftherealms.tools.snes.assembler.definition.directives.AllDirectives;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode65816;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCodeZ80;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.bank.BankNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control.IfBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.DefinitionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.EnumNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.OperationType;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition.StructNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroBodyNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro.MacroNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.SectionNode.SectionStatus;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.expression.*;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor.MacroDefinitionVisitor;
import net.sagaoftherealms.tools.snes.assembler.pass.scan.token.TokenTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.BANK;
import static net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section.RamsectionArgumentsNode.RamsectionArguments.NAME;
import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.$;
import static net.sagaoftherealms.tools.snes.assembler.util.TestUtils.asParser;
import static org.junit.jupiter.api.Assertions.*;

public class SourceParserTest {

  @Test
  public void testExpressionParser() {
    var sourceLine = "NUM_SEED_TREES*8";
    var parser = asParser(sourceLine);

    var expressionNode = ExpressionParser.expressionNode(parser);

    assertEquals(NodeTypes.IDENTIFIER_EXPRESSION, expressionNode.getChildren().get(0).getType());
    assertEquals(NodeTypes.NUMERIC_CONSTANT, expressionNode.getChildren().get(1).getType());
    assertEquals(
        OperationType.MULTIPLY, ((NumericExpressionNode) expressionNode).getOperationType());
  }

  @Test
  public void testExpressionParser2() {
    var sourceLine = "\\2|:\\2";
    var parser = asParser(sourceLine);

    var expressionNode = ExpressionParser.expressionNode(parser);

    assertEquals(NodeTypes.IDENTIFIER_EXPRESSION, expressionNode.getChildren().get(0).getType());
    assertEquals(NodeTypes.IDENTIFIER_EXPRESSION, expressionNode.getChildren().get(1).getType());
    assertEquals(OperationType.OR, ((NumericExpressionNode) expressionNode).getOperationType());
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
    var parser = asParser(sourceLine);

    LabelDefinitionNode node = (LabelDefinitionNode) parser.nextNode();
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
  public void multiFileTest() throws IOException {
    var sourceDirectory = "ages-disasm";
    var sourceRoot = "main.s";
    var includedFile = "ages-disasm/objects/macros.s";
    long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    MultiFileParser multiParser = new MultiFileParser(OpCodeZ80.opcodes());
    multiParser.parse(sourceDirectory, sourceRoot);
    // Runtime.getRuntime().gc();
    long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

    System.out.println(afterUsedMem - beforeUsedMem);

    assertNotNull(multiParser.getNodes(includedFile));
    assertEquals(
        "obj_Conditional",
        ((MacroNode) ((List<Node>) multiParser.getNodes(includedFile)).get(1)).getName());
  }

  /**
   * When sourceparser is run on a file, it may encounter macros defined in other files that it
   * hasn't found yet. This test tests that those trees get reparsed.
   *
   * @throws IOException
   */
  @Test
  public void testMultiFileParserGeneratesCorrectParseTreeWithMacros() throws IOException {
    var sourceDirectory = "parseMacro";
    var sourceRoot = "define_macro_4.s";

    MultiFileParser multiParser = new MultiFileParser(OpCodeZ80.opcodes());
    multiParser.parse(sourceDirectory, sourceRoot);

    var nodes = multiParser.getNodes(sourceDirectory + File.separator + sourceRoot);
    assertEquals(MacroCallNode.class, nodes.get(0).getClass());
  }
  /**
   * When sourceparser is run on a file, it may encounter macros defined in other files that it
   * hasn't found yet. This test tests that those trees get reparsed.
   *
   * @throws IOException
   */
  @Test
  public void testMultiFileParserGeneratesCorrectParseTreeWithMacros2() throws IOException {
    var sourceDirectory = "parseMacro_2";
    var sourceRoot = "define_macro_4.s";
    var sourceInclude = "define_macro_3.s";

    MultiFileParser multiParser = new MultiFileParser(OpCodeZ80.opcodes());
    multiParser.parse(sourceDirectory, sourceRoot);

    var nodes = multiParser.getNodes(sourceDirectory + File.separator + sourceInclude);
    assertEquals(MacroNode.class, nodes.get(0).getClass());

    nodes = multiParser.getNodes(sourceDirectory + File.separator + sourceRoot);
    assertEquals(MacroCallNode.class, nodes.get(0).getClass());
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
  @CsvSource({"'.DBCOS 0.2, 10, 3.2, 120.0, 1.3\n', DBCOS, '[.2,10,3.2,120.0,1.3]'"})
  public void testParseDirectiveWithArgumentsToken(
      String sourceLine,
      String expectedDirective,
      @ConvertWith(DoubleArrayConverter.class) List<Double> arguments) {
    var parser = asParser(sourceLine);

    DirectiveNode node = (DirectiveNode) parser.nextNode();

    assertEquals(arguments.size(), node.getArguments().size());
  }

  @ParameterizedTest
  @CsvSource({".DBCOS 0.2"})
  public void testParsingDirectivesFailWithTooFewArgumentsToken(String sourceLine) {
    var parser = asParser(sourceLine);

    assertEquals(NodeTypes.ERROR, parser.nextNode().getType());
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
    var parser = asParser(enumSource);

    assertEquals(NodeTypes.ERROR, parser.nextNode().getType());
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
            + "; Tree refill data also used for child and an visitor in room $2f7\n"
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

    var parser = asParser(source);

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
    var parser = asParser(enumSource);
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
    var parser = asParser(enumSource);
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
    var parser = asParser(source);
    var ifNode = (DirectiveNode) parser.nextNode();
    var thenNode = (DirectiveBodyNode) ifNode.getBody().getChildren().get(0);
    var elseNode = (DirectiveBodyNode) ifNode.getBody().getChildren().get(1);

    assertEquals(
        "Two", ((DirectiveNode) thenNode.getChildren().get(0)).getArguments().getString(1));
    assertFalse(
            ((DirectiveNode) thenNode.getChildren().get(0)).hasBody());
    assertEquals("5", ((DirectiveNode) elseNode.getChildren().get(0)).getArguments().getString(1));
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

  @ParameterizedTest
  @CsvSource({
    "+", "-", "++", "--", "++++", "----",
  })
  public void testAnonymousLabelsEvaluate(String line) {
    var parser = asParser(line);
    IdentifierNode node = (IdentifierNode) ExpressionParser.expressionNode(parser);
    assertEquals(line, node.getLabelName());
  }

  @Test
  public void testMemorymap() {
    var source =
        ".MEMORYMAP\n"
            + "DEFAULTSLOT 1\n"
            + "SLOTSIZE $2000\n"
            + "SLOT 0 $0000\n"
            + "SLOTSIZE $6000\n"
            + "SLOT 1 $2000\n"
            + ".ENDME";
    var parser = asParser(source);
    var gbNode = (DirectiveNode) parser.nextNode();
    assertEquals(5, gbNode.getBody().getChildren().size());
  }

  @Test
  public void testRommap() {
    var source =
        ".ROMBANKMAP\n"
            + "BANKSTOTAL 2\n"
            + "BANKSIZE $2000\n"
            + "BANKS 1\n"
            + "BANKSIZE $6000\n"
            + "BANKS 1\n"
            + ".ENDRO";
    var parser = asParser(source);
    var gbNode = (DirectiveNode) parser.nextNode();
    assertEquals(5, gbNode.getBody().getChildren().size());
  }

  @Test
  public void testGBSection() {
    var source =
        ".GBHEADER\n"
            + "    NAME \"TANKBOMBPANIC\"  ; identical to a freestanding .NAME.\n"
            + "    LICENSEECODEOLD $34   ; identical to a freestanding .LICENSEECODEOLD.\n"
            + "    LICENSEECODENEW \"HI\"  ; identical to a freestanding .LICENSEECODENEW.\n"
            + "    CARTRIDGETYPE $00     ; identical to a freestanding .CARTRIDGETYPE.\n"
            + "    RAMSIZE $09           ; identical to a freestanding .RAMSIZE.\n"
            + "    COUNTRYCODE $01       ; identical to a freestanding .COUNTRYCODE/DESTINATIONCODE.\n"
            + "    DESTINATIONCODE $01   ; identical to a freestanding .DESTINATIONCODE/COUNTRYCODE.\n"
            + "    NINTENDOLOGO          ; identical to a freestanding .NINTENDOLOGO.\n"
            + "    VERSION $01           ; identical to a freestanding .VERSION.\n"
            + "    ROMDMG                ; identical to a freestanding .ROMDMG.\n"
            + "                          ; Alternatively, ROMGBC or ROMGBCONLY can be used\n"
            + ".ENDGB";

    var parser = asParser(source);
    var gbNode = (DirectiveNode) parser.nextNode();
    assertEquals(10, gbNode.getBody().getChildren().size());
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
            + ".ENDIF; COMMENT\n"
            + ".ELSE\n"
            +
            // 7 = monster 8 = monster.name 12 = monster.1.age 17 = monster.3.name
            "     dragon    INSTANCEOF mon   ; one mon\n"
            + ".ENDIF\n"
            + // 21 dragon.age
            ".ENDE";

    var parser = asParser(source);
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
   * DB includes strings, DW does not. The test sources have DB with string, but I want to enforce
   * the failure case.
   */
  public void testDWFailsWithString() {
    String source = ".dw \"Fail\"";
    var parser = asParser(source);
    assertEquals(NodeTypes.ERROR, parser.nextNode().getType());
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

    var parser = asParser(source);

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

    var parser = asParser(source);

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
    var parser = asParser(source);
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

    var data = new InputData();
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    SourceParser parser = new SourceParser(scanner);

    assertEquals(NodeTypes.ERROR, parser.nextNode().getType());
  }

  @Test
  public void exceptionIfNoEnde() {

    final String enumSource = ".ENUM $C000\n";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    SourceParser parser = new SourceParser(scanner);
    Assertions.assertEquals(NodeTypes.ERROR, parser.nextNode().getType());
  }

  /**
   * Sections can have a lot of permuations of type, size, etc. See the section Node for the stuff I
   * will need to write.
   */
  @ParameterizedTest
  @CsvSource({
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" SIZE 100 ALIGN 4 FORCE RETURNORG APPENDTO \"appended\", FORCE, 100, 4, RETURNORG, appended",
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" FREE, FREE, , , , ",
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" SIZE 84 ALIGN 100 SUPERFREE RETURNORG APPENDTO \"appendix\", SUPERFREE, 84, 100, RETURNORG, appendix",
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" SEMIFREE,SEMIFREE, , , , ",
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" SEMISUBFREE,SEMISUBFREE, , , , ",
    ".SECTION \"EmptyVectors\" NAMESPACE \"bank0\" OVERWRITE,OVERWRITE, , , , "
  })
  public void testSectionBasic(
      String section,
      String status,
      Integer size,
      Integer align,
      String returnOrg,
      String appendTo) {
    final String enumSource =
        section + "\n" + "\n" + "EmptyHandler:\n" + "       rti\n" + "\n" + ".ENDS\n" + ".8BIT\n";
    final String outfile = "test.out";
    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    SourceParser parser = new SourceParser(scanner);

    SectionNode node = (SectionNode) parser.nextNode();
    assertEquals(AllDirectives.SECTION, node.getDirectiveType());
    assertEquals("EmptyVectors", node.getName());
    assertEquals(SectionStatus.valueOf(status), node.getStatus());
    assertEquals(align, node.getAlignment());
    assertEquals(!isNullOrEmpty(returnOrg), node.isAdvanceOrg());
    assertEquals(appendTo, node.getAppendTo());
    assertEquals("bank0", node.getNamespace());

    Node emptyHandlerLabelNode = node.getBody().getChildren().get(0);
    assertEquals(NodeTypes.LABEL_DEFINITION, emptyHandlerLabelNode.getType());
    Node rtiOpLabel = node.getBody().getChildren().get(1);
    assertEquals(NodeTypes.OPCODE, rtiOpLabel.getType());
    DirectiveNode eightBit = (DirectiveNode) parser.nextNode();
    assertNotNull(eightBit);
  }

  @Test
  public void testSectionBasic2() {
    final String enumSource =
        ".SECTION \"EmptyVectors\" SEMIFREE "
            + "\n"
            + "\n"
            + "EmptyHandler:\n"
            + "       rti\n"
            + "\n"
            + ".ENDS\n";

    final String inputFile = "test.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(enumSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCode65816.opcodes());

    SourceParser parser = new SourceParser(scanner);

    SectionNode node = (SectionNode) parser.nextNode();
    assertEquals(NodeTypes.DIRECTIVE, node.getType());
    assertEquals(AllDirectives.SECTION, node.getDirectiveType());
    assertEquals("EmptyVectors", node.getName());
  }

  /** macro_1 is a basic macro with no variables or lookups or anything. */
  @Test
  public void testDefineMacro1BasicMacro() throws IOException {
    final String macroSource =
        new BufferedReader(
                new InputStreamReader(
                    SourceParserTest.class
                        .getClassLoader()
                        .getResourceAsStream("parseMacro/define-macro-1.s")))
            .lines()
            .collect(Collectors.joining("\n"));

    final String outfile = "define_macro_1.out";
    final String inputFile = "define_macro_1.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());

    SourceParser parser = new SourceParser(scanner);

    MacroNode node = (MacroNode) parser.nextNode();
    assertEquals("wait_1s", node.getName());

    var body = node.getBody();
    assertEquals(NodeTypes.MACRO_BODY, body.getChildren().get(0).getType());
    assertEquals(TokenTypes.LABEL, body.getChildren().get(1).getSourceToken().getType());
  }

  /** macro_2 is a basic macro with two variables */
  @Test
  public void testDefineMacro2DeclaredVariables() throws IOException {
    final String macroSource =
        new BufferedReader(
                new InputStreamReader(
                    SourceParserTest.class
                        .getClassLoader()
                        .getResourceAsStream("parseMacro/define_macro_2.s")))
            .lines()
            .collect(Collectors.joining("\n"));
    final String outfile = "define_macro_2.out";
    final String inputFile = "parseMacro/define_macro_2.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());

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
        new BufferedReader(
                new InputStreamReader(
                    SourceParserTest.class
                        .getClassLoader()
                        .getResourceAsStream("parseMacro/define_macro_3.s")))
            .lines()
            .collect(Collectors.joining("\n"));
    final String outfile = "define_macro_3.out";
    final String inputFile = "parseMacro/define_macro_3.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());

    SourceParser parser = new SourceParser(scanner);

    MacroNode node = (MacroNode) parser.nextNode();

    var arguments = node.getArguments();
    assertEquals(1, arguments.size());
    var body = node.getBody();
    MacroBodyNode dbNode = (MacroBodyNode) body.getChildren().get(0);

    assertEquals(8, body.getChildren().size());
    assertEquals("\\1", body.getChildren().get(3).getSourceToken().getString());
    assertEquals(TokenTypes.EOL, body.getChildren().get(7).getSourceToken().getType());
  }

  @ParameterizedTest
  @CsvSource({
    "2*81, 162",
    "2+1, 3",
    "21-1, 20",
    "20/2, 10",
    "(20 + 2)/2, 11",
    "20 + (2/2), 21",
    "8 | 2, 10",
    "7 & 4, 4",
    "-5 + 5, 0",
    "2<<1, 4",
    "512 >> 8 != 1024 >> 8, 1 ",
    "512 >> 8 != 2, 0 ",
    "2>>1, 1",
    "<$DEAD, 173",
    ">$DEAD, 222"
  })
  public void testExpressions(String expression, int value) {
    final String outfile = "script_commands.out";
    final String inputFile = "script_commands.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(expression), inputFile, lineNumber);
    var scanner = data.startRead(OpCodeZ80.opcodes());

    SourceParser parser = new SourceParser(scanner);

    var node = ExpressionParser.expressionNode(parser);
    assertEquals(value, (int) node.evaluate());
  }

  @Test
  public void parseBanks() {
    var parser = asParser(".BANK $FF SLOT 4");
    BankNode node = (BankNode) parser.nextNode();
    assertEquals(0xFF, ((DirectiveNode) node).getArguments().getInt(0));
    assertEquals(4, ((DirectiveNode) node).getArguments().getInt(1));
  }

  /** macro_3 is a basic macro with labels inside that refer to macro arguments by number */
  @ParameterizedTest
  @CsvSource({
    "parseLargeFiles/script_commands.s",
    "parseLargeFiles/main.s",
    "ages-disasm/include/musicMacros.s",
    "ages-disasm/include/rominfo.s",
    "ages-disasm/include/structs.s"
  })
  public void testLargeFile(String fileName) throws IOException {

    final String macroSource =
        new BufferedReader(
                new InputStreamReader(
                    SourceParserTest.class.getClassLoader().getResourceAsStream(fileName)))
            .lines()
            .collect(Collectors.joining("\n"));
    final String outfile = fileName + ".out";
    final String inputFile = fileName;
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(macroSource), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());

    SourceParser parser = new SourceParser(scanner);

    var node = parser.nextNode();

    while (node != null) {
      System.out.println(node);
      node = parser.nextNode();
    }
  }

  @Test
  public void evilExpression() {
    var parser = asParser("12*\\1+0 + OFFSET");
    ExpressionNode expression = ExpressionParser.expressionNode(parser);
    assertEquals(
        NodeTypes.IDENTIFIER_EXPRESSION,
        expression.getChildren().get(1).getChildren().get(1).getType());
    assertEquals(
        NodeTypes.NUMERIC_CONSTANT, expression.getChildren().get(0).getChildren().get(0).getType());
  }

  @Test
  public void testMacroVisitorBuildsTableOfMacroNames() {
    var program =
        ";\nwriteobjectword 17 18\n"
            + "writeobjectword 19, 512\n"
            + ".MACRO writeobjectword\n"
            + "writeobjectbyte \\1,   \\2&$ff\n"
            + "\twriteobjectbyte \\1+1, \\2>>$8\n"
            + ".ENDM\n";

    final String inputFile = "parseLargeFiles/script_commandss.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(program), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());
    SourceParser parser = new SourceParser(scanner);
    var visitor = new MacroDefinitionVisitor();
    parser.addVisitor(visitor);

    var writeobjectwordCall1 = (LabelDefinitionNode) parser.nextNode();
    parser.nextNode();
    parser.nextNode();
    var writeobjectwordCall2 = (LabelDefinitionNode) parser.nextNode();
    parser.nextNode();
    parser.nextNode();
    parser.nextNode();

    assertTrue(visitor.getMacroNames().containsKey(writeobjectwordCall1.getLabelName()));
    assertTrue(visitor.getMacroNames().containsKey(writeobjectwordCall2.getLabelName()));
  }

  @Test
  public void testMacroCall() {
    var program =
        "\n\n"
            + ".MACRO writeobjectword\n"
            + "writeobjectbyte \\1,   \\2&$ff\n"
            + "\twriteobjectbyte \\1+1, \\2>>$8\n"
            + ".ENDM\n"
            + ".MACRO writeobjectbyte\n"
            + "\t.db $8e \\1 \\2\n"
            + "\n"
            + ".ENDM"
            + "\n\n"
            + "writeobjectword 17 18\n"
            + "writeobjectword 19, 512";

    final String outfile = "script_commands.out";
    final String inputFile = "parseLargeFiles/script_commands.s";
    final int lineNumber = 0;

    var data = new InputData();
    data.includeFile($(program), inputFile, lineNumber);

    var scanner = data.startRead(OpCodeZ80.opcodes());
    SourceParser parser = new SourceParser(scanner);
    var writeobjectwordMacro = (MacroNode) parser.nextNode();
    var writeobjectbyteMacro = (MacroNode) parser.nextNode();
    var writeobjectwordCall1 = (MacroCallNode) parser.nextNode();
    var writeobjectwordCall2 = (MacroCallNode) parser.nextNode();
    assertEquals(
        17, (int) ((NumericExpressionNode) writeobjectwordCall1.getArguments().get(0)).evaluate());
    assertEquals(
        512, (int) ((NumericExpressionNode) writeobjectwordCall2.getArguments().get(1)).evaluate());
    assertEquals(writeobjectwordMacro.getName(), writeobjectwordCall2.getMacroNode());
    assertEquals(
        "writeobjectbyte",
        writeobjectwordMacro.getBody().getChildren().get(0).getSourceToken().getString());

    // Check that all nodes are created
    int writeobjectbyteMacronodeSize = 0;

    for (var node : writeobjectwordMacro) {
      System.out.println(node.getSourceToken());
      writeobjectbyteMacronodeSize++;
    }

    assertEquals(21, writeobjectbyteMacronodeSize);
  }

  @Test
  public void testIncludeDirective() {
    var source =
        ".include \"constants/areaFlags.s\"\n"
            + ".include \"constants/breakableTileSources.s\"\n"
            + ".include \"constants/directions.s\"\n"
            + ".include \"constants/collisionEffects.s\"\n"
            + ".include \"constants/collisionTypes.s\"";

    var parser = asParser(source);

    DirectiveNode node1 = (DirectiveNode) parser.nextNode();
    DirectiveNode node2 = (DirectiveNode) parser.nextNode();
    DirectiveNode node3 = (DirectiveNode) parser.nextNode();
    DirectiveNode node4 = (DirectiveNode) parser.nextNode();
    DirectiveNode node5 = (DirectiveNode) parser.nextNode();

    var arguments3 = node3.getArguments();
    var fileName = arguments3.getString(0);
    var fileName2 = arguments3.getChildren().get(0).getSourceToken().getString();

    assertEquals(fileName, fileName2);
    assertEquals("constants/directions.s", fileName);
    assertEquals("constants/directions.s", fileName2);
  }

  @Test
  public void testStringExpressions() {
    var parser = asParser(".INCLUDE \"test\"");
    var directive = (DirectiveNode) parser.nextNode();

    assertEquals("test", directive.getArguments().getString(0));
  }

  @Test
  public void testDirectiveError() {
    var source = ".kaboom";
    var parser = asParser(source, OpCodeZ80.opcodes());
    ErrorNode opcode = (ErrorNode) parser.nextNode();
    assertEquals(1, parser.getErrors().size());
  }

  @Test
  public void testOpcodes() {
    var source = "ld l,a";
    var parser = asParser(source, OpCodeZ80.opcodes());
    OpcodeNode opcode = (OpcodeNode) parser.nextNode();
    assertEquals(2, opcode.getChildren().size());
    assertEquals("l", ((OpcodeArgumentNode) opcode.getChildren().get(0)).getToken().getString());
    assertEquals("a", ((OpcodeArgumentNode) opcode.getChildren().get(1)).getToken().getString());
  }

  @Test
  public void testRedef() {
    var source = ".REDEFINE a 4+2";
    var parser = asParser(source, OpCodeZ80.opcodes());
    var node = (DirectiveNode) parser.nextNode();
    assertEquals(2, node.getArguments().size());
    assertEquals("a", (node.getArguments().getString(0)));
    assertEquals(6, node.getArguments().getInt(1));
  }

  @Test
  public void testDefineWithNumberWordSize() {
    var source = ".DEFINE random_seed\t\t$0001.w";
    var parser = asParser(source, OpCodeZ80.opcodes());
    var node = (DirectiveNode) parser.nextNode();
    assertEquals(AllDirectives.DEFINE, node.getDirectiveType());
    assertEquals("random_seed", (node.getArguments().getString(0)));
    assertEquals(1, node.getArguments().getInt(1));
    assertEquals(
        Sizes.SIXTEEN_BIT,
        ((NumericExpressionNode) node.getArguments().getChildren().get(1)).getSize());
  }

  @Test
  public void testParseErrors() {
    List<ErrorNode> errors = new ArrayList<>();
    var source = ".if 1\n .include \n .endif"; // If true  {errorNode} end;

    var parser = asParser(source);
    DirectiveNode ifNode = (DirectiveNode) parser.nextNode();
    assertEquals(
        NodeTypes.ERROR,
        ((DirectiveBodyNode) (ifNode.getBody().getChildren().get(0)))
            .getChildren()
            .get(0)
            .getType());
  }

  private boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }
}
