package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.main.Project;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MemoryMapErrorAnalyzerTest {

  @Test
  @Disabled
  public void canScan() throws URISyntaxException {
    Project project =
        new Project.Builder((getClass().getClassLoader().getResource("snes-hello-world").toURI()))
            .build();
    var nodes = project.getNodes("main.s");
    assertNotNull(nodes);
  }

  @Test
  @DisplayName("Slot IDs must be in order")
  public void inOrder() {
    var code =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                    DEFAULTSLOT 0\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                   SLOT 2 $16000                  ; Defines Slot 16's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(5, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("There can be only one")
  public void testOnlyOneMemoryMapDirective() {
    var mainS =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                   DEFAULTSLOT 0\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition\n"
            + "                 .MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                    SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                    DEFAULTSLOT 0\n"
            + "                    SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                  .ENDME          ; End MemoryMap definition";
    var parser = TestUtils.asParser(mainS);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(6, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Default slot is required to be set")
  public void testDefaultSlotSet() {
    var code =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Default slot can only be defined once")
  public void testMultipleDefaultSlot() {
    var code =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                    DEFAULTSLOT 0\n"
            + "                    DEFAULTSLOT 0\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(4, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Slot must have a default size")
  public void testDefaultSlotSize() {
    var code =
        "                .MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   DEFAULTSLOT 0\n"
            + "                   SLOT 0 $8000       ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(3, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Default slot must be < 255")
  public void testDefaultSlotSizeLimit() {
    var code =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000\n"
            + "                   DEFAULTSLOT 256\n"
            + "                   SLOT 0 $8000       ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(3, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Memory Map is analyzed without errors")
  public void testGoldenScenario() {
    var code =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                    DEFAULTSLOT 0\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition";

    var parser = TestUtils.asParser(code);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(0, errors.size());
  }

  @Test
  @DisplayName("Multiple Errors should be reported")
  public void testMultipleErrors() {
    var mainS =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                 .ENDME          ; End MemoryMap definition\n"
            + "                 .MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "                    SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + "                    DEFAULTSLOT 0\n"
            + "                    SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + "                  .ENDME          ; End MemoryMap definition";
    var parser = TestUtils.asParser(mainS);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(2, errors.size());
    assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);
    assertEquals(5, errors.get(1).getSourceToken().getPosition().beginLine);
  }
}
