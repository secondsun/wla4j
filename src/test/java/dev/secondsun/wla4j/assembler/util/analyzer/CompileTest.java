package dev.secondsun.wla4j.assembler.util.analyzer;

import dev.secondsun.wla4j.assembler.util.compiler.SourceCompiler;
import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.analyzer.SourceAnalyzer;
import dev.secondsun.wla4j.assembler.main.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompileTest {
    @Test
    @DisplayName("Import the hello world project and check for errors")
    public void fullAnalyze(){
        Project helloWorldProject =
                new Project.Builder(new File("src/test/resources/snes-hello-world").toURI()).build();

        Context ctx = new Context();
        SourceAnalyzer analyzer = new SourceAnalyzer(ctx);

        var mainNodes = helloWorldProject.getNodes("main.s");
        var headerNodes = helloWorldProject.getNodes("Header.s");
        var initNodes = helloWorldProject.getNodes("Snes_Init.s");

        assertTrue(analyzer.analyzeProject("main.s", mainNodes).isEmpty());
        assertTrue(analyzer.analyzeProject("Snes_Init.s", initNodes).isEmpty());
        var headerErrors = analyzer.analyzeProject("Header.s", headerNodes);
        assertTrue(headerErrors.isEmpty());

    }

    @Test
    @DisplayName("Compile Hello World into a rom")
    public void compileHelloWorld(){
        Project helloWorldProject =
                new Project.Builder(new File("src/test/resources/snes-hello-world").toURI()).build();

        Context ctx = new Context();
        SourceAnalyzer analyzer = new SourceAnalyzer(ctx);

        var mainNodes = helloWorldProject.getNodes("main.s");
        var headerNodes = helloWorldProject.getNodes("Header.s");
        var initNodes = helloWorldProject.getNodes("Snes_Init.s");

        assertTrue(analyzer.analyzeProject("main.s", mainNodes).isEmpty());
        assertTrue(analyzer.analyzeProject("Snes_Init.s", initNodes).isEmpty());
        var headerErrors = analyzer.analyzeProject("Header.s", headerNodes);
        assertTrue(headerErrors.isEmpty());

        SourceCompiler compiler = new SourceCompiler(helloWorldProject, ctx);
        assertNotEquals(0, compiler.compile().length);
    }
}
