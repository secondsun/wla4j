package net.sagaoftherealms.tools.snes.assembler.util.compiler;

import net.sagaoftherealms.tools.snes.assembler.analyzer.Context;
import net.sagaoftherealms.tools.snes.assembler.main.Project;

public class SourceCompiler {
    private final Project project;
    private final Context context;

    public SourceCompiler(Project helloWorldProject, Context ctx) {
        this.project = helloWorldProject;
        this.context = ctx;
    }

    public byte[] compile(){
        return new byte[0];
    }

}
