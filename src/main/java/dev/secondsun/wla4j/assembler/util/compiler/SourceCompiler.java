package dev.secondsun.wla4j.assembler.util.compiler;

import dev.secondsun.wla4j.assembler.analyzer.Context;
import dev.secondsun.wla4j.assembler.main.Project;

public class SourceCompiler {
  private final Project project;
  private final Context context;

  public SourceCompiler(Project helloWorldProject, Context ctx) {
    this.project = helloWorldProject;
    this.context = ctx;
  }

  public byte[] compile() {
    return new byte[0];
  }
}
