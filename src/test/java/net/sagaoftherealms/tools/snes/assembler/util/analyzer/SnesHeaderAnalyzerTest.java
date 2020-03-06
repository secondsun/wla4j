package net.sagaoftherealms.tools.snes.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.analyzer.Context;
import net.sagaoftherealms.tools.snes.assembler.analyzer.SourceAnalyzer;
import net.sagaoftherealms.tools.snes.assembler.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SnesHeaderAnalyzerTest {

  @Test
  @DisplayName("SNES Header Must be defined only once")
  public void snesHeaderTestOnlyDefinedOnce() {
    var source = """
                .SNESHEADER
                  ID "Test"
                .ENDSNES
                .SNESHEADER
                ID "Test"
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("id Must Be Between One And Four Characters")
  public void idMustBeBetweenOneAndFourCharacters() {
    var source = """
                .SNESHEADER
                  ID "SNES12"
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("id Must Be Between One And Four Characters")
  public void idMustBeBetweenOneAndFourCharacters2() {
    var source = """
                .SNESHEADER
                  ID ""
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }


  @Test
  @DisplayName("Name Must Be Between 1 And 21 Characters")
  public void nameMustBeBetweenOneAnd21Characters() {
    var source = """
                .SNESHEADER
                  ID "SNES"
                  NAME "1234567890123456789012"
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("Name  Must Be Between 1 And 21 Characters")
  public void nameMustBeBetweenOneAnd21Characters2() {
    var source = """
                .SNESHEADER
                  ID "SNES"
                  NAME ""
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("ROM Mode must be set only once")
  public void setRomModeOnlyOnce() {
    var source = """
                .SNESHEADER
                  ID "SNES"
                  NAME "This Name"
                  LOROM
                  HIROM
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }

  @Test
  @DisplayName("Cartridge Type must be set only once")
  public void setCartridgeTypeOnlyOnce() {
    var source = """
                .SNESHEADER
                  ID "SNES"
                  NAME "This Name"
                  HIROM
                  CARTRIDGETYPE $FF
                  CARTRIDGETYPE $01
                .ENDSNES
                """;

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }


}
