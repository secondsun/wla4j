package dev.secondsun.wla4j.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.util.TestUtils;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BankSizeTest {

  @Test
  @DisplayName("There can be only one")
  public void testOnlyOneRomBankSize() {

    var mainS = ".ROMBANKSIZE $8000\n" + ".ROMBANKSIZE $8000";
    var parser = TestUtils.asParser(mainS);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(2, errors.get(0).getSourceToken().getPosition().beginLine);
  }

  @Test
  @DisplayName("There can be only one")
  public void testOnlyOneBankSize() {

    var mainS = ".BANKSIZE $8000\n" + ".BANKSIZE $8000\n";

    var parser = TestUtils.asParser(mainS);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(2, errors.get(0).getSourceToken().getPosition().beginLine);
  }
}
