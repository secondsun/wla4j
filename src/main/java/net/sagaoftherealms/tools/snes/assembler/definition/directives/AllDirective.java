package net.sagaoftherealms.tools.snes.assembler.definition.directives;

public class AllDirective extends Directive {

    
    public AllDirective(String s) {
        super(s.split(" ")[0].replace(".",""), s);
        
    }
}
