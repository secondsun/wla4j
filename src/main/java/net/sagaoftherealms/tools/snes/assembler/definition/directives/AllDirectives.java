package net.sagaoftherealms.tools.snes.assembler.definition.directives;

public enum AllDirectives {
    ;

    private final String name;
    private final String pattern;

    private AllDirectives(Directive directive) {
        this.name = directive.getName();
        this.pattern = directive.getPattern();
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }
}
