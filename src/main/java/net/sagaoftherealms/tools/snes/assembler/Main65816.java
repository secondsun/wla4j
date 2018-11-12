package net.sagaoftherealms.tools.snes.assembler;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_NONE;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_OBJECT;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.FAILED;
import static net.sagaoftherealms.tools.snes.assembler.main.Flags.Result.SUCCEEDED;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import net.sagaoftherealms.tools.snes.assembler.main.Flags;
import net.sagaoftherealms.tools.snes.assembler.main.InputData;

/**
 * @author summers
 */
public class Main65816 {

  private static final String wla_version = "65816 Macro Assembler for Java based on WLA 65816 Macro Assembler v9.8a.";
  private static HashMap defines_map;
  private static HashMap global_unique_label_map;
  private static HashMap namespace_map;
  private static boolean commandline_parsing;

  private static Flags flags;
  private static String finalName;
  private static File gba_tmp_file;


  public static void main(String... args) {
    try {
      defines_map = new HashMap();
      global_unique_label_map = new HashMap();
      namespace_map = new HashMap();
      flags = new Flags(args);

      if (flags.getOutputFormat() == OUTPUT_NONE) {
        if (flags.getResult() == SUCCEEDED) {
          /* assume object file output name */
          flags.setOutputFormat(OUTPUT_OBJECT);
          finalName = flags.getAsmName() + ".o";
        }
      }

      if (flags.getOutputFormat() == OUTPUT_NONE || flags.getResult() == FAILED) {
        printDefaultMessage();
        return;
      }

      if (flags.getAsmName().equals(finalName)) {
        throw new RuntimeException("Input and output files have same name");
      }

      gba_tmp_file = File.createTempFile("wla", "tmp");


      /* small inits */
      if (flags.isExtraDefinitions()) {
        generate_extra_definitions();
      }

      commandline_parsing = false;

      /* start the process */

      InputData data = new InputData(flags);
      data.includeFile(flags.getAsmName());
//            new Pass1(data).pass();
//
//
//            if (pass_1() == FAILED)
//
//                if (pass_2() == FAILED)
//                    return 1;
//            if (pass_3() == FAILED)
//                return 1;
//            if (flags.listfile_data == YES) {
//                if (listfile_collect() == FAILED)
//                    return 1;
//            }
//            if (pass_4() == FAILED)
//                return 1;
//
//            return 0;

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      proceduresAtExit();
    }
  }


  private static void generate_extra_definitions() {
    String dateString;

    /* generate WLA_TIME */
    dateString = new Date().toString();

    flags.add_a_new_definition("WLA_TIME", 0.0, dateString, DEFINITION_TYPE_STRING);
    flags.add_a_new_definition("wla_time", 0.0, dateString, DEFINITION_TYPE_STRING);
    flags.add_a_new_definition("WLA_VERSION", 0.0, wla_version, DEFINITION_TYPE_STRING);
    flags.add_a_new_definition("wla_version", 0.0, wla_version, DEFINITION_TYPE_STRING);


  }


  private static void printDefaultMessage() {
    System.out.println("65816 Macro Assembler for Java based on ");
    System.out.println("WLA 65816 Macro Assembler v9.8a");
    System.out.println("Java port by secondsun https://github.com/secondsun/snes-dev-tools");
    System.out.println(
        "Based on codoe written by Ville Helin in 1998-2008 - In GitHub since 2014: https://github.com/vhelin/wla-dx\n");
    System.out.println(("USAGE: Main [OPTIONS] <ASM FILE>"));
    System.out.println("Options:");
    System.out.println("-i  Add list file information");
    System.out.println("-M  Output makefile rules");
    System.out.println("-q  Quiet");
    System.out.println("-t  Test compile");
    System.out.println("-v  Verbose messages");
    System.out.println("-x  Extra compile time definitions");
    System.out.println("-I [DIR]  Include directory");
    System.out.println("-D [DEF]  Declare definition");
    System.out.println("Output types:");
    System.out.println("-o [FILE]  Output object file");
    System.out.println("-l [FILE]  Output library file");
  }

  private static void proceduresAtExit() {
    if (gba_tmp_file != null) {
      gba_tmp_file.deleteOnExit();
    }
  }


}
