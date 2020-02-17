package net.sagaoftherealms.tools.snes.assembler.analyzer;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ErrorNode;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.DirectiveNode;

import java.util.List;

public class RomBanksAnalyzer {
    private final Context context;

    public RomBanksAnalyzer(Context context) {
        this.context = context;

    }

    public List<? extends ErrorNode> checkDirective(DirectiveNode node) {
        return null;
    }
}
