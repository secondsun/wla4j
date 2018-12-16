package net.sagaoftherealms.tools.snes.assembler.definition.directives;

import java.util.Random;

public enum AllDirectives {
  // x = a whole number
  // f = a number with a decimal part
  // c = a character
  // s = a String value (expands to "some text"
  // l = a label (which will be a string)

  // t = a boolean expression
  // e = a integer expression

  // {x|y} One or more of x, y...
  // []{} = A comma separated List of types in the braces (see .DB in
  // https://wla-dx.readthedocs.io/en/latest/asmdiv.html)

  // ? = Optional

  // (pattern) a pattern
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
  BASE(new AllDirective(".BASE x")),
  BLOCK(new AllDirective(".BLOCK s")),
  ENDB(new AllDirective(".ENDB")),
  BANK(new AllDirective(".BANK x SLOT x")),
  SLOT(new AllDirective(".SLOT x")),
  ROMBANKSIZE(new AllDirective(".ROMBANKSIZE x")),
  ORG(new AllDirective(".ORG c")),
  ORGA(new AllDirective(".ORGA c")),
  DS(new AllDirective(".DS x, x")),
  DSB(new AllDirective(".DSB x, x")),
  DSTRUCT(new AllDirective(".DSTRUCT l ?(INSTANCEOF) l ?(DATA) []{xsf}")),
  DSW(new AllDirective(".DSW x, x")),
  DB(new AllDirective(".DB []{xsf}")),
  BYT(new AllDirective(".BYT []{xsf}")),
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
  WORD(new AllDirective(".WORD .DW ")),
  DWM(new AllDirective(".DWM l []{x}")),
  DEFINE(new AllDirective(".DEFINE l []{xs}")),
  DEF(new AllDirective(".DEF l []{xs}")),
  EQU(new AllDirective(".EQU l []{xs}")),
  REDEFINE(new AllDirective(".REDEFINE l []{xsL}")),
  REDEF(new AllDirective(".REDEF l []{xsL}")),
  IF(new AllDirective(".IF t")),
  IFDEF(new AllDirective(".IFDEF l")),
  IFEXISTS(new AllDirective(".IFEXISTS s")),
  UNDEFINE(new AllDirective(".UNDEFINE l")),
  UNDEF(new AllDirective(".UNDEF l")),
  IFNDEF(new AllDirective(".IFNDEF l")),
  IFDEFM(new AllDirective(".IFDEFM \\x")),
  IFNDEFM(new AllDirective(".IFNDEFM \\x")),
  IFEQ(new AllDirective(".IFEQ {elx} {elx}")),
  IFNEQ(new AllDirective(".IFNEQ {elx} {elx}")),
  IFLE(new AllDirective(".IFLE {elx} {elx}")),
  IFLEEQ(new AllDirective(".IFLEEQ {elx} {elx}")),
  IFGR(new AllDirective(".IFGR {elx} {elx}")),
  IFGREQ(new AllDirective(".IFGREQ {elx} {elx}")),
  ELSE(new AllDirective(".ELSE")),
  ENDIF(new AllDirective(".ENDIF")),
  REPEAT(new AllDirective(".REPEAT x ?(INDEX l)")),
  REPT(new AllDirective(".REPT x ?(INDEX l)")),
  ENDR(new AllDirective(".ENDR")),
  ENUM(new AllDirective(".ENUM x ?{ASC|DESC} ?(EXPORT)")),
  ENDE(new AllDirective(".ENDE")),
  STRUCT(new AllDirective(".STRUCT l")),
  ENDST(new AllDirective(".ENDST")),
  MEMORYMAP(new AllDirective(".MEMORYMAP")),
  ENDME(new AllDirective(".ENDME")),
  ROMBANKMAP(new AllDirective(".ROMBANKMAP")),
  ENDRO(new AllDirective(".ENDRO")),
  SEED(new AllDirective(".SEED x")),
  SECTION_BANKSECTION(
      new AllDirective(
          ".SECTION \"BANKSECTION\" ?(NAMESPACE s) ?(SIZE x) ?(ALIGN x) ?{FORCE|FREE|SUPERFREE|SEMIFREE|SEMISUBFREE|OVERWRITE} ?(APPENDTO l)")),
  SECTION(
      new AllDirective(
          ".SECTION {sl} ?(NAMESPACE s) ?(SIZE x) ?(ALIGN x) ?{FORCE|FREE|SUPERFREE|SEMIFREE|SEMISUBFREE|OVERWRITE} ?(APPENDTO l)")),
  RAMSECTION(new AllDirective(".RAMSECTION l ?(BANK x) ?(SLOT x) ?(ALIGN x) ?(APPENDTO l)")),
  ENDS(new AllDirective(".ENDS")),
  EXPORT(new AllDirective(".EXPORT []{l}")),
  PRINTT(new AllDirective(".PRINTT s")),
  PRINTV(new AllDirective(".PRINTV ?{DEC|HEX} e")),
  OUTNAME(new AllDirective(".OUTNAME s")),
  SNESHEADER(new AllDirective(".SNESHEADER")),
  ENDSNES(new AllDirective(".ENDSNES")),
  SNESNATIVEVECTOR(new AllDirective(".SNESNATIVEVECTOR")),
  ENDNATIVEVECTOR(new AllDirective(".ENDNATIVEVECTOR")),
  SNESEMUVECTOR(new AllDirective(".SNESEMUVECTOR")),
  ENDEMUVECTOR(new AllDirective(".ENDEMUVECTOR")),
  ;

  private final String name;
  private final String pattern;

  AllDirectives(Directive directive) {
    this.name = directive.getName();
    this.pattern = directive.getPattern();
  }

  public static String generateDirectiveLine(String pattern, boolean skipFirst) {
    Random r = new Random();
    StringBuilder builder = new StringBuilder();
    int patternIndex = 0;

    char patternCharacter = pattern.charAt(patternIndex);

    while (patternCharacter != ' ' && skipFirst) {

      builder.append(patternCharacter);
      patternIndex = patternIndex + 1;
      if (patternIndex < pattern.length()) {
        patternCharacter = pattern.charAt(patternIndex);
      } else {
        break;
      }
    }

    for (; patternIndex <= pattern.length(); patternIndex++) {
      if (patternIndex < pattern.length()) {
        patternCharacter = pattern.charAt(patternIndex);
      } else {
        break;
      }
      switch (patternCharacter) {
        case ')':
          break;
        case 'x':
          builder.append(r.nextInt(256));
          break;
        case 'f':
          builder.append(r.nextInt(256)).append(".").append(r.nextInt(10));
          break;
        case 'c':
          builder.append((char) (r.nextInt(26) + 'A'));
          break;
        case 's':
        case 'l':
          builder.append('"').append(randomString(10)).append('"');
          break;
        case 't':
          builder.append("x != y");
          break;
        case 'e':
          builder.append("(5 + 6)");
          break;
        case '{': {
          patternIndex++;
          var newPatternBuilder = new StringBuilder();
          var test = pattern.charAt(patternIndex);
          while (test != '}') {
            newPatternBuilder.append(test);
            test = pattern.charAt(++patternIndex);
          }

          var newPattern = newPatternBuilder.toString();
          builder.append(
              generateDirectiveLine(
                  "" + newPattern.charAt(r.nextInt(newPattern.length() - 2) + 1), false));
        }
        break;
        case '[': {
          patternIndex++; // ]
          if (pattern.charAt(++patternIndex) == '(') {

            var newPatternBuilder = new StringBuilder();
            newPatternBuilder.append('(');
            var test = pattern.charAt(++patternIndex);
            while (test != ')') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }
            newPatternBuilder.append(')');

            builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
            builder.append(',');
            builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
            builder.append(',');
            builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
          } else {

            var newPatternBuilder = new StringBuilder();
            newPatternBuilder.append('{');
            var test = pattern.charAt(++patternIndex);
            while (test != '}') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }
            newPatternBuilder.append('}');
            var newPattern = newPatternBuilder.toString();

            builder.append(
                generateDirectiveLine(
                    "" + newPattern.charAt(r.nextInt(newPattern.length() - 2) + 1), false));
            builder.append(',');
            builder.append(
                generateDirectiveLine(
                    "" + newPattern.charAt(r.nextInt(newPattern.length() - 2) + 1), false));
            builder.append(',');
            builder.append(
                generateDirectiveLine(
                    "" + newPattern.charAt(r.nextInt(newPattern.length() - 2) + 1), false));
          }
        }
        break;
        case '?': {
          //                    if (!r.nextBoolean()) {
          //                        break;
          //                    }
          patternIndex++; // ?

          if (pattern.charAt(patternIndex++) == '(') { // (
            var newPatternBuilder = new StringBuilder();
            newPatternBuilder.append('(');
            var test = pattern.charAt(patternIndex);
            while (test != ')') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }
            newPatternBuilder.append(')');
            builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
          } else {
            // assume }
            var newPatternBuilder = new StringBuilder();

            var test = pattern.charAt(patternIndex);
            while (test != '}') {
              newPatternBuilder.append(test);
              test = pattern.charAt(++patternIndex);
            }

            String[] choices = newPatternBuilder.toString().split("\\|");
            builder.append(choices[r.nextInt(choices.length)]);
          }
        }
        break;
        case '(': {
          patternIndex++; // (

          var newPatternBuilder = new StringBuilder();
          var test = pattern.charAt(patternIndex);
          while (test != ')') {
            newPatternBuilder.append(test);
            test = pattern.charAt(++patternIndex);
          }
          builder.append(generateDirectiveLine(newPatternBuilder.toString(), false));
        }
        break;

        default:
          builder.append(patternCharacter);
      }
    }
    return builder.toString();
  }

  private static String randomString(int i) {
    var r = new Random();
    var b = new StringBuilder(i);
    for (int x = 0; x < i; x++) {
      b.append((char) (r.nextInt(26) + 'A'));
    }
    return b.toString();
  }

  public String getName() {
    return name;
  }

  public String getPattern() {
    return pattern;
  }

  // x = a whole number
  // f = a number with a decimal part
  // c = a character
  // s = a String value (expands to "some text"
  // l = a label (which will be a string)

  // t = a boolean expression
  // e = a integer expression

  // {x|y} One or more of x, y...
  // {}[] = A comma separated List of types in the parenthesis (see .DB in
  // https://wla-dx.readthedocs.io/en/latest/asmdiv.html)

  // ? = Optional

  // (pattern) a pattern

}
