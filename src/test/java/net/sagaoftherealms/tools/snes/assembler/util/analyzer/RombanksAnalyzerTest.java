package net.sagaoftherealms.tools.snes.assembler.util.analyzer;

import net.sagaoftherealms.tools.snes.assembler.analyzer.Context;
import net.sagaoftherealms.tools.snes.assembler.analyzer.SourceAnalyzer;
import net.sagaoftherealms.tools.snes.assembler.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RombanksAnalyzerTest {


    @Test
    @DisplayName("banksized must be defined")
    public void testBanksizeRequired() {
        var source = """
                .ROMBANKS 8
                """;

        var parser = TestUtils.asParser(source);
        var nodes = List.of(parser.nextNode(), parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);
    }


    @Test
    @DisplayName("Golden Scenario")
    public void goldenScenario() {
        var source = """
                 .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                   DEFAULTSLOT 0
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 .ROMBANKSIZE $8000              ; Every ROM bank is 32 KBytes in size
                 .ROMBANKS 8                     ; 2 Mbits - Tell WLA we want to use 8 ROM Banks
                """;


        var parser = TestUtils.asParser(source);
        var nodes = List.of(parser.nextNode(), parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(0, errors.size());
    }

}
