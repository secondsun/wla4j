package net.sagaoftherealms.tools.snes.assembler.util.analyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.sagaoftherealms.tools.snes.assembler.analyzer.Context;
import net.sagaoftherealms.tools.snes.assembler.analyzer.SourceAnalyzer;
import net.sagaoftherealms.tools.snes.assembler.util.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BankSizeTest {

  @Test
  @DisplayName("There can be only one")
  public void testOnlyOneRomBankSize() {

    var mainS = """
          .ROMBANKSIZE $8000
          .ROMBANKSIZE $8000
        """;
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

    var mainS = """
          .BANKSIZE $8000
          .BANKSIZE $8000
        """;
    var parser = TestUtils.asParser(mainS);
    var nodes = List.of(parser.nextNode(), parser.nextNode());

    SourceAnalyzer checker = new SourceAnalyzer(new Context());
    var errors = checker.analyzeProject("main.s", nodes);
    assertEquals(1, errors.size());
    assertEquals(2, errors.get(0).getSourceToken().getPosition().beginLine);


  }


}
