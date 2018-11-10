package net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;

import net.sagaoftherealms.tools.snes.assembler.pass.parse.ParseException;
import net.sagaoftherealms.tools.snes.assembler.pass.parse.SourceParser;

public interface DirectiveParser {
    DirectiveBodyNode body(SourceParser parser);
    DirectiveArgumentsNode arguments(SourceParser parser) throws ParseException;
}
