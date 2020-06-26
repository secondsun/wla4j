package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SnesHeaderAnalyzerTest {

  @Test
  @DisplayName("SNES Header Must be defined only once")
  public void snesHeaderTestOnlyDefinedOnce() {
    var source =
        ".SNESHEADER\n"
            + "                  ID \"Test\"\n"
            + "                .ENDSNES\n"
            + "                .SNESHEADER\n"
            + "                ID \"Test\"\n"
            + "                .ENDSNES";

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
    var source = ".SNESHEADER\n" + "                  ID \"SNES12\"\n" + "                .ENDSNES";

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
    var source =
        "\n"
            + "                .SNESHEADER\n"
            + "                  ID \"\"\n"
            + "                .ENDSNES";

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
    var source =
        " .SNESHEADER\n"
            + "                  ID \"SNES\"\n"
            + "                  NAME \"1234567890123456789012\"\n"
            + "                .ENDSNES";

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
    var source =
        ".SNESHEADER\n"
            + "                  ID \"SNES\"\n"
            + "                  NAME \"\"\n"
            + "                .ENDSNES";

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
    var source =
        ".SNESHEADER\n"
            + "                  ID \"SNES\"\n"
            + "                  NAME \"This Name\"\n"
            + "                  LOROM\n"
            + "                  HIROM\n"
            + "                .ENDSNES";

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
    var source =
        ".SNESHEADER\n"
            + "                  ID \"SNES\"\n"
            + "                  NAME \"This Name\"\n"
            + "                  HIROM\n"
            + "                  CARTRIDGETYPE $FF\n"
            + "                  CARTRIDGETYPE $01\n"
            + "                .ENDSNES";

    var parser = TestUtils.asParser(source);
    var nodes = List.of(parser.nextNode());

    Context ctx = new Context();

    SourceAnalyzer checker = new SourceAnalyzer(ctx);
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
  }
}
