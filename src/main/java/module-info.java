open module wla4j {
  requires java.logging;
  requires jakarta.json;

  exports dev.secondsun.wla4j.assembler.main;
  exports dev.secondsun.wla4j.assembler.analyzer;
  exports dev.secondsun.wla4j.assembler.pass.parse.visitor;
  exports dev.secondsun.wla4j.assembler.definition.directives;
  exports dev.secondsun.wla4j.assembler.definition.opcodes;
  exports dev.secondsun.wla4j.assembler.pass.parse.bank;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.definition;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.control;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.gbheader;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.incbin;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.macro;
  exports dev.secondsun.wla4j.assembler.pass.parse.directive.section;
  exports dev.secondsun.wla4j.assembler.pass.parse.expression;
  exports dev.secondsun.wla4j.assembler.pass.scan.token;
  exports dev.secondsun.wla4j.assembler.pass.parse;
  exports dev.secondsun.wla4j.assembler.util;
}
