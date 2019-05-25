package net.sagaoftherealms.tools.snes.assembler.definition.directives;

/**
 * x = a whole number f = a number with a decimal part c = a character s = a String value (expands
 * to "some text" l = a label (which will be a string)
 *
 * <p>{x|y} One or more of x, y... []{} = A comma separated List of types in the braces (see .DB in
 * https://wla-dx.readthedocs.io/en/latest/asmdiv.html)
 *
 * <p>? = Optional
 *
 * <p>(pattern) a pattern
 */
public enum AllDirectives {
  EIGHT_BIT(new AllDirective(".8BIT")),
  SIXTEEN_BIT(new AllDirective(".16BIT")),
  TWENTYFOUR_BIT(new AllDirective(".24BIT")),
  ACCU(new AllDirective(".ACCU x")),
  INDEX(new AllDirective(".INDEX x")),
  ASM(new AllDirective(".ASM")),
  ENDASM(new AllDirective(".ENDASM")),
  DBRND(new AllDirective(".DBRND x, x, x")),
  DWRND(new AllDirective(".DWRND x, x, x")),
  DBCOS(new AllDirective(".DBCOS f, x, f, f, f")),
  DBSIN(new AllDirective(".DBSIN f, x, f, f, f")),
  DWCOS(new AllDirective(".DWCOS f, x, f, f, f")),
  DWSIN(new AllDirective(".DWSIN f, x, f, f, f")),
  ROMBANKS(new AllDirective(".ROMBANKS x")),
  EMPTYFILL(new AllDirective(".EMPTYFILL x")),
  COMPUTESNESCHECKSUM(new AllDirective(".COMPUTESNESCHECKSUM")),
  INCDIR(new AllDirective(".INCDIR s")),
  INCLUDE(new AllDirective(".INCLUDE s")),
  INCBIN(new AllDirective(".INCBIN s")),
  INPUT(new AllDirective(".INPUT NAME")),
  BACKGROUND(new AllDirective(".BACKGROUND s")),
  UNBACKGROUND(new AllDirective(".UNBACKGROUND x x")),
  FAIL(new AllDirective(".FAIL")),
  FCLOSE(new AllDirective(".FCLOSE l")),
  FOPEN(new AllDirective(".FOPEN s l")),
  FREAD(new AllDirective(".FREAD l l")),
  FSIZE(new AllDirective(".FSIZE l l")),
  MACRO(new AllDirective(".MACRO l")),
  ENDM(new AllDirective(".ENDM")),
  SHIFT(new AllDirective(".SHIFT")),
  FASTROM(new AllDirective(".FASTROM")),
  SLOWROM(new AllDirective(".SLOWROM")),
  SMC(new AllDirective(".SMC")),
  HIROM(new AllDirective(".HIROM")),
  EXHIROM(new AllDirective(".EXHIROM")),
  LOROM(new AllDirective(".LOROM")),
  BASE(new AllDirective(".BASE e")),
  BLOCK(new AllDirective(".BLOCK s")),
  ENDB(new AllDirective(".ENDB")),
  BANK(new AllDirective(".BANK x SLOT x")),
  SLOT(new AllDirective(".SLOT x")),
  ROMBANKSIZE(new AllDirective(".ROMBANKSIZE x")),
  BANKSIZE(new AllDirective(".BANKSIZE x")),
  ORG(new AllDirective(".ORG e")),
  ORGA(new AllDirective(".ORGA e")),
  DS(new AllDirective(".DS x, x")),
  DSB(new AllDirective(".DSB x, x")),
  DSTRUCT(new AllDirective(".DSTRUCT l ?(INSTANCEOF) l ?(DATA) []{xsf}")),
  DSW(new AllDirective(".DSW x, x")),
  DB(new AllDirective(".DB []{lxsf}")),
  BYTE(new AllDirective(".BYTE []{lxsf}")),
  BYT(new AllDirective(".BYT []{lxsf}")),
  DBM(new AllDirective(".DBM l []{xsf}")),
  SYM(new AllDirective(".SYM l")),
  SYMBOL(new AllDirective(".SYMBOL l")),
  BR(new AllDirective(".BR")),
  BREAKPOINT(new AllDirective(".BREAKPOINT")),
  ASCIITABLE(new AllDirective(".ASCIITABLE")),
  ENDA(new AllDirective(".ENDA")),
  ASCTABLE(new AllDirective(".ASCTABLE")),
  ASC(new AllDirective(".ASC s")),
  DW(new AllDirective(".DW []{xc}")),
  WORD(new AllDirective(".WORD []{xc} ")),
  DWM(new AllDirective(".DWM l []{x}")),
  DEFINE(new AllDirective(".DEFINE l []{xs}")),
  DEF(new AllDirective(".DEF l []{xs}")),
  EQU(new AllDirective(".EQU l []{xs}")),
  REDEFINE(new AllDirective(".REDEFINE l []{se}")),
  REDEF(new AllDirective(".REDEF l []{xsL}")),
  IF(new AllDirective(".IF e")),
  IFDEF(new AllDirective(".IFDEF l")),
  IFEXISTS(new AllDirective(".IFEXISTS s")),
  UNDEFINE(new AllDirective(".UNDEFINE l")),
  UNDEF(new AllDirective(".UNDEF l")),
  IFNDEF(new AllDirective(".IFNDEF l")),
  IFDEFM(new AllDirective(".IFDEFM \\x")),
  IFNDEFM(new AllDirective(".IFNDEFM \\x")),
  IFEQ(new AllDirective(".IFEQ e e")),
  IFNEQ(new AllDirective(".IFNEQ e e")),
  IFLE(new AllDirective(".IFLE e e")),
  IFLEEQ(new AllDirective(".IFLEEQ e e")),
  IFGR(new AllDirective(".IFGR e e")),
  IFGREQ(new AllDirective(".IFGREQ e e")),
  ELSE(new AllDirective(".ELSE")),
  ENDIF(new AllDirective(".ENDIF")),
  REPEAT(new AllDirective(".REPEAT x ?(INDEX l)")),
  REPT(new AllDirective(".REPT x ?(INDEX l)")),
  ENDR(new AllDirective(".ENDR")),
  ENUM(new AllDirective(".ENUM x ?{ASC|DESC} ?(EXPORT)")), /*EnumParser*/
  ENDE(new AllDirective(".ENDE")), /*EnumParser*/
  STRUCT(new AllDirective(".STRUCT l")), /*StructParser*/
  ENDST(new AllDirective(".ENDST")), /*StructParser*/
  MEMORYMAP(new AllDirective(".MEMORYMAP")),
  ENDME(new AllDirective(".ENDME")),
  ROMBANKMAP(new AllDirective(".ROMBANKMAP")),
  ENDRO(new AllDirective(".ENDRO")),
  SEED(new AllDirective(".SEED x")),
  SECTION_BANKSECTION(
      new AllDirective(
          ".SECTION \"BANKHEADER\" ?(NAMESPACE s) ?(SIZE x) ?(ALIGN x) ?{FORCE|FREE|SUPERFREE|SEMIFREE|SEMISUBFREE|OVERWRITE} ?(APPENDTO l)")),
  SECTION(
      new AllDirective(
          ".SECTION {sl} ?(NAMESPACE s) ?(SIZE x) ?(ALIGN x) ?{FORCE|FREE|SUPERFREE|SEMIFREE|SEMISUBFREE|OVERWRITE} ?(APPENDTO l)")),
  RAMSECTION(new AllDirective(".RAMSECTION {ls} ?(BANK x) ?(SLOT x) ?(ALIGN x) ?(APPENDTO l)")),
  ENDS(new AllDirective(".ENDS")),
  ENDGB(new AllDirective(".ENDGB")),
  EXPORT(new AllDirective(".EXPORT []{l}")),
  PRINTT(new AllDirective(".PRINTT {sl}")),
  PRINTV(new AllDirective(".PRINTV ?{DEC|HEX} e")),
  OUTNAME(new AllDirective(".OUTNAME s")),
  SNESHEADER(new AllDirective(".SNESHEADER")),
  ENDSNES(new AllDirective(".ENDSNES")),
  SNESNATIVEVECTOR(new AllDirective(".SNESNATIVEVECTOR")),
  ENDNATIVEVECTOR(new AllDirective(".ENDNATIVEVECTOR")),
  SNESEMUVECTOR(new AllDirective(".SNESEMUVECTOR")),
  ENDEMUVECTOR(new AllDirective(".ENDEMUVECTOR")),
  COMPUTEGBCHECKSUM(new AllDirective(".COMPUTEGBCHECKSUM")),
  CARTRIDGETYPE(new AllDirective(".CARTRIDGETYPE x")),
  COUNTRYCODE(new AllDirective(".COUNTRYCODE x")),
  VERSION(new AllDirective(".VERSION x")),
  DESTINATIONCODE(new AllDirective(".DESTINATIONCODE x")),
  NINTENDOLOGO(new AllDirective(".NINTENDOLOGO")),
  GBHEADER(new AllDirective(".GBHEADER")),
  SMSHEADER(new AllDirective(".SMSHEADER")),
  COMPUTEGBCOMPLEMENTCHECK(new AllDirective(".COMPUTEGBCOMPLEMENTCHECK")),
  LICENSEECODENEW(new AllDirective(".LICENSEECODENEW s")),
  LICENSEECODEOLD(new AllDirective(".LICENSEECODEOLD x")),
  NAME(new AllDirective(".NAME s")),
  RAMSIZE(new AllDirective(".RAMSIZE x")),
  ROMDMG(new AllDirective(".ROMDMG")),
  ROMGBC(new AllDirective(".ROMGBC")),
  ROMGBCONLY(new AllDirective(".ROMGBCONLY")),
  ROMSGB(new AllDirective(".ROMSGB")),
  ;

  private final String name;
  private final String pattern;

  AllDirectives(Directive directive) {
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
