open module wla_parser {
  requires java.logging;
  requires java.json;

  exports net.sagaoftherealms.tools.snes.assembler.main;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.visitor;
  exports net.sagaoftherealms.tools.snes.assembler.definition.directives;
  exports net.sagaoftherealms.tools.snes.assembler.definition.opcodes;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.bank;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.definition;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.control;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.gbheader;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.incbin;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.macro;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.directive.section;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse.expression;
  exports net.sagaoftherealms.tools.snes.assembler.pass.scan.token;
  exports net.sagaoftherealms.tools.snes.assembler.pass.parse;
  exports net.sagaoftherealms.tools.snes.assembler.util;
}
