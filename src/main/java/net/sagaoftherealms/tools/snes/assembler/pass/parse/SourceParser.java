package net.sagaoftherealms.tools.snes.assembler.pass.parse;

import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;

public class SourceParser {
    private final SourceScanner scanner;

    public SourceParser(SourceScanner scanner) {
        this.scanner = scanner;
    }

    public Node nextNode() {
        return null;
    }
}
