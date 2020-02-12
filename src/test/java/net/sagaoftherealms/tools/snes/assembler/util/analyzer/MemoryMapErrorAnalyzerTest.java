package net.sagaoftherealms.tools.snes.assembler.util.analyzer;

import net.sagaoftherealms.tools.snes.assembler.analyzer.Context;
import net.sagaoftherealms.tools.snes.assembler.analyzer.MemoryMapAnalyzer;
import net.sagaoftherealms.tools.snes.assembler.main.Project;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryMapErrorAnalyzerTest {

    @Test
    public void canScan() throws URISyntaxException {
        Project project = new Project.Builder((getClass().getClassLoader().getResource("snes-hello-world").toURI())).build();
        var nodes = project.getNodes("main.s");
        assertNotNull(nodes);
    }

    @Test
    @DisplayName("Slot IDs must be in order")
    public void inOrder(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                    DEFAULTSLOT 0
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                   SLOT 2 $16000                  ; Defines Slot 16's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

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
        var mainS = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                   DEFAULTSLOT 0
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 .MEMORYMAP                      ; Begin describing the system architecture.
                    SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                    DEFAULTSLOT 0
                    SLOT 0 $8000                  ; Defines Slot 0's starting address.
                  .ENDME          ; End MemoryMap definition
                """;
        var parser = TestUtils.asParser(mainS);
        var nodes = List.of(parser.nextNode(), parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(6, errors.get(0).getSourceToken().getPosition().beginLine);


    }

    @Test
    @DisplayName("Default slot is required to be set")
    public void testDefaultSlotSet(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

        var parser = TestUtils.asParser(code);
        var nodes = List.of(parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);


    }

    @Test
    @DisplayName("Default slot can only be defined once")
    public void testMultipleDefaultSlot(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                    DEFAULTSLOT 0
                    DEFAULTSLOT 0
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

        var parser = TestUtils.asParser(code);
        var nodes = List.of(parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(4, errors.get(0).getSourceToken().getPosition().beginLine);

    }

    @Test
    @DisplayName("Slot must have a default size")
    public void testDefaultSlotSize(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   DEFAULTSLOT 0
                   SLOT 0 $8000       ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

        var parser = TestUtils.asParser(code);
        var nodes = List.of(parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(3, errors.get(0).getSourceToken().getPosition().beginLine);

    }
    @Test
    @DisplayName("Default slot must be < 255")
    public void testDefaultSlotSizeLimit(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000
                   DEFAULTSLOT 256
                   SLOT 0 $8000       ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

        var parser = TestUtils.asParser(code);
        var nodes = List.of(parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(3, errors.get(0).getSourceToken().getPosition().beginLine);

    }


    @Test
    @DisplayName("Memory Map is analyzed without errors")
    public void testGoldenScenario(){
        var code = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                    DEFAULTSLOT 0
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 """;

        var parser = TestUtils.asParser(code);
        var nodes = List.of(parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(0, errors.size());

    }


    @Test
    @DisplayName("Multiple Errors should be reported")
    public void testMultipleErrors() {
        var mainS = """
                .MEMORYMAP                      ; Begin describing the system architecture.
                   SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                   SLOT 0 $8000                  ; Defines Slot 0's starting address.
                 .ENDME          ; End MemoryMap definition
                 .MEMORYMAP                      ; Begin describing the system architecture.
                    SLOTSIZE $8000                ; The slot is $8000 bytes in size. More details on slots later.
                    DEFAULTSLOT 0
                    SLOT 0 $8000                  ; Defines Slot 0's starting address.
                  .ENDME          ; End MemoryMap definition
                """;
        var parser = TestUtils.asParser(mainS);
        var nodes = List.of(parser.nextNode(), parser.nextNode());

        SourceAnalyzer checker = new SourceAnalyzer(new Context());
        var errors = checker.analyzeProject("main.s", nodes);
        assertEquals(2, errors.size());
        assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);
        assertEquals(5, errors.get(1).getSourceToken().getPosition().beginLine);

    }

}
