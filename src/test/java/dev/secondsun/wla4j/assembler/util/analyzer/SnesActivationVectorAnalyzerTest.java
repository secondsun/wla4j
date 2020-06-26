package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SnesActivationVectorAnalyzerTest {

  private static final String HEADER =
      ".SNESHEADER\n"
          + " ID \"SNES\"                     ; 1-4 letter string, just leave it as \"SNES\"\n"
          + "NAME \"SNES Tile Demo       \"  ; Program Title - can't be over 21 bytes,\n"
          + ";    \"123456789012345678901\"  ; use spaces for unused bytes of the name.\n"
          + "SLOWROM\n"
          + "LOROM\n"
          + "CARTRIDGETYPE $00             ; $00 = ROM only, see WLA documentation for others\n"
          + "ROMSIZE $08                   ; $08 = 2 Mbits,  see WLA doc for more..\n"
          + "SRAMSIZE $00                  ; No SRAM         see WLA doc for more..\n"
          + "COUNTRY $01                   ; $01 = U.S.  $00 = Japan  $02 = Australia, Europe, Oceania and Asia  $03 = Sweden  $04 = Finland  $05 = Denmark  $06 = France  $07 = Holland  $08 = Spain  $09 = Germany, Austria and Switzerland  $0A = Italy  $0B = Hong Kong and China  $0C = Indonesia  $0D = Korea\n"
          + "LICENSEECODE $00              ; Just use $00\n"
          + "VERSION $00                   ; $00 = 1.00, $01 = 1.01, etc.\n"
          + ".ENDSNES\n";

  @Test
  @DisplayName("SNES NATIVE VECTOR needs to have the romtype defined")
  public void romTypeMustBeDefined() {
    var source =
        ".SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table\n"
            + "COP EmptyHandler\n"
            + "BRK EmptyHandler\n"
            + "ABORT EmptyHandler\n"
            + "NMI VBlank\n"
            + "IRQ EmptyHandler\n"
            + ".ENDNATIVEVECTOR\n";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("SNES NATIVE VECTOR  defined only once")
  public void mustBeDefinedOnlyOnce() {
    var source =
        HEADER
            + ".SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table\n"
            + "COP EmptyHandler\n"
            + "BRK EmptyHandler\n"
            + "ABORT EmptyHandler\n"
            + "NMI VBlank\n"
            + "IRQ EmptyHandler\n"
            + ".ENDNATIVEVECTOR\n"
            + ".SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table\n"
            + "  COP EmptyHandler\n"
            + "  BRK EmptyHandler\n"
            + " ABORT EmptyHandler\n"
            + "  NMI VBlank\n"
            + "  IRQ EmptyHandler\n"
            + ".ENDNATIVEVECTOR\n";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode(), parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }
}
