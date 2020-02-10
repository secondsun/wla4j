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
    public void handleMemoryMap(){
        //There can be only one
        //it has a default slot
        //default slot is between 0 and 255
        // slot is between 0 and 255
        //slotsize must be defined if slot doesn't have a size
        //slot ids must be in order
        //slot  starting address has to be a non-negative value.
        fail("Make above individual tests");
    }

    @Test
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

        MemoryMapAnalyzer checker = new MemoryMapAnalyzer(new Context());
        List<ErrorNode> errors = checker.check("main.s", nodes);
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

        MemoryMapAnalyzer checker = new MemoryMapAnalyzer(new Context());
        List<ErrorNode> errors = checker.check("main.s", nodes);
        assertEquals(1, errors.size());
        assertEquals(1, errors.get(0).getSourceToken().getPosition().beginLine);


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

        MemoryMapAnalyzer checker = new MemoryMapAnalyzer(new Context());
        List<ErrorNode> errors = checker.check("main.s", nodes);
        assertEquals(0, errors.size());


    }
}
