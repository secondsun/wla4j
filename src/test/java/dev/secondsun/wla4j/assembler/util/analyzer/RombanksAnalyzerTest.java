package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RombanksAnalyzerTest {

  @Test
  @DisplayName("banksize must be defined")
  public void testBanksizeRequired() {
    var source = ".ROMBANKS 8\n";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("Rombanks may not change : This is a break from how wla-dx handles it")
  public void testBanksizeMayNotChange() {
    var source =
        ".ROMBANKSIZE $8000              ; Every ROM bank is 32 KBytes in size\n"
            + ".ROMBANKS 8\n"
            + ".ROMBANKS 12\n";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode(), parser.nextNode(), parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("Golden Scenario")
  public void goldenScenario() {
    var source =
        ".MEMORYMAP                      ; Begin describing the system architecture.\n"
            + "  SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.\n"
            + " DEFAULTSLOT 0\n"
            + "   SLOT 0 $8000                  ; Defines Slot 0's starting address.\n"
            + ".ENDME          ; End MemoryMap definition\n"
            + ".ROMBANKSIZE $8000              ; Every ROM bank is 32 KBytes in size\n"
            + ".ROMBANKS 8                     ; 2 Mbits - Tell WLA we want to use 8 ROM Banks\n";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode(), parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(0, errors.size());
  }
}
