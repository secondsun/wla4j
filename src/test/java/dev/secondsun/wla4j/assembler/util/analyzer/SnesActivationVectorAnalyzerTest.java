package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SnesActivationVectorAnalyzerTest {

  private static final String HEADER = """

       .SNESHEADER
         ID "SNES"                     ; 1-4 letter string, just leave it as "SNES"

         NAME "SNES Tile Demo       "  ; Program Title - can't be over 21 bytes,
         ;    "123456789012345678901"  ; use spaces for unused bytes of the name.

         SLOWROM
         LOROM

         CARTRIDGETYPE $00             ; $00 = ROM only, see WLA documentation for others
         ROMSIZE $08                   ; $08 = 2 Mbits,  see WLA doc for more..
         SRAMSIZE $00                  ; No SRAM         see WLA doc for more..
         COUNTRY $01                   ; $01 = U.S.  $00 = Japan  $02 = Australia, Europe, Oceania and Asia  $03 = Sweden  $04 = Finland  $05 = Denmark  $06 = France  $07 = Holland  $08 = Spain  $09 = Germany, Austria and Switzerland  $0A = Italy  $0B = Hong Kong and China  $0C = Indonesia  $0D = Korea
         LICENSEECODE $00              ; Just use $00
         VERSION $00                   ; $00 = 1.00, $01 = 1.01, etc.
       .ENDSNES

      """;

  @Test
  @DisplayName("SNES NATIVE VECTOR needs to have the romtype defined")
  public void romTypeMustBeDefined() {
    var source = """
         .SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table
           COP EmptyHandler
           BRK EmptyHandler
           ABORT EmptyHandler
           NMI VBlank
           IRQ EmptyHandler
         .ENDNATIVEVECTOR
        """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());

  }

  @Test
  @DisplayName("SNES NATIVE VECTOR  defined only once")
  public void mustBeDefinedOnlyOnce() {
    var source = HEADER+ """
         .SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table
           COP EmptyHandler
           BRK EmptyHandler
           ABORT EmptyHandler
           NMI VBlank
           IRQ EmptyHandler
         .ENDNATIVEVECTOR
         .SNESNATIVEVECTOR               ; Define Native Mode interrupt vector table
           COP EmptyHandler
           BRK EmptyHandler
           ABORT EmptyHandler
           NMI VBlank
           IRQ EmptyHandler
         .ENDNATIVEVECTOR
        """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());

  }
}
