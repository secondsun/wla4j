package net.sagaoftherealms.tools.snes.assembler.main;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;
import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_VALUE;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_LIBRARY;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_NONE;
import static net.sagaoftherealms.tools.snes.assembler.Defines.Output.OUTPUT_OBJECT;
import static net.sagaoftherealms.tools.snes.assembler.Defines.YesNo.NO;
import static net.sagaoftherealms.tools.snes.assembler.Defines.YesNo.YES;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sagaoftherealms.tools.snes.assembler.Defines;
import net.sagaoftherealms.tools.snes.assembler.Definition;

/** This class parses and represents flags. */
public class Flags {

  private static final int MAX_NAME_LENGTH = 255;
  private final Result result;
  private final String[] flags;
  private Map<String, Definition> definitions = new HashMap<>();
  private Defines.Output outputFormat = OUTPUT_NONE;
  private String finalName;
  private String extIncDir;
  private boolean useExtIncDir = false;
  private boolean listFileData = false;
  private boolean verboseMode = false;
  private boolean testMode = false;
  private boolean makefileRules = false;
  private boolean quiet = false;
  private boolean extraDefinitions = false;
  private String asmName;

  public Flags(String... flags) {
    this.flags = flags;
    this.result = parse();
  }

  public Result getResult() {
    return this.result;
  }

  public Result parse() {
    int count;
    int asmnameDef = 0;
    StringBuilder strBuild = null;
    int flagc = flags.length;

    for (count = 0; count < flags.length; count++) {

      switch (flags[count]) {
        case "-o":
          {
            if (outputFormat != OUTPUT_NONE) {
              return Result.FAILED;
            }
            outputFormat = OUTPUT_OBJECT;
            if (count + 1 < flagc) {
              /* set output */
              finalName = flags[count + 1];
            } else {
              return Result.FAILED;
            }

            count++;
            break;
          }

        case "-l":
          {
            if (outputFormat != OUTPUT_NONE) {
              throw new RuntimeException(outputFormat + " != OUTPUT_NONE");
            }
            outputFormat = OUTPUT_LIBRARY;
            if (count + 1 < flagc) {
              /* set output */
              finalName = flags[count + 1];
            } else {
              throw new RuntimeException("parse error");
            }
            count++;
            break;
          }

        case "-D":
          {
            if (count + 1 < flagc) {
              if (count + 3 < flagc) {
                if (!(flags[count + 2].equals("="))) {
                  strBuild =
                      new StringBuilder(
                          (flags[count + 1].length()) + (flags[count + 3]).length() + 2);
                  strBuild.append(String.format("%s=%s", flags[count + 1], flags[count + 3]));
                  parseAndAddDefinition(strBuild.toString(), NO);
                  strBuild = null;
                  count += 2;
                } else {
                  parseAndAddDefinition(flags[count + 1], NO);
                }
              } else {
                parseAndAddDefinition(flags[count + 1], NO);
              }
            } else {
              throw new RuntimeException("parse error");
            }

            count++;
            break;
          }

        case "-I":
          {
            if (count + 1 < flagc) {
              /* getString arg */
              parseAndSetIncdir(flags[count + 1], NO);
            } else {
              throw new RuntimeException("Unknown Flag " + flags[count] + ":" + flags[count + 1]);
            }
            count++;
            break;
          }
        case "-i":
          {
            listFileData = true;
            break;
          }
        case "-v":
          {
            verboseMode = true;
            break;
          }
        case "-t":
          {
            testMode = true;
            break;
          }
        case "-M":
          {
            makefileRules = true;
            testMode = true;
            verboseMode = false;
            quiet = true;
            break;
          }
        case "-q":
          {
            quiet = true;
            break;
          }
        case "-x":
          {
            extraDefinitions = true;
            break;
          }
        default:
          {
            if (count == flags.length - 1) {
              asmName = (flags[count]);
              count++;
              asmnameDef++;
            } else {
              /* legacy support? */
              if (flags[count].equals("-D")) {
                /* old define */
                parseAndAddDefinition(flags[count], YES);
              } else if (flags[count].equals("-I")) {
                /* old include directory */
                parseAndSetIncdir(flags[count], YES);
                continue;
              } else {
                throw new RuntimeException("Unknown Flag " + flags[count]);
              }
            }
            break;
          }
      }
    }

    if (asmnameDef <= 0) {
      throw new RuntimeException("No assembly name definition passed.");
    }

    return Result.SUCCEEDED;
  }

  private Result parseAndSetIncdir(String flag, Defines.YesNo containsFlag) {
    try {
      String name;
      int i;

      /* skip the flag? */
      if (containsFlag == YES) {
        flag = flag.substring(2);
      }

      name = flag;

      extIncDir = name + File.separatorChar;
      useExtIncDir = true;

      return Result.SUCCEEDED;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void parseAndAddDefinition(String flag, Defines.YesNo containsFlag) {

    String n;
    String flagsAfterSplit[];

    int i;

    /* skip the flag? */
    if (containsFlag == YES) {
      flag = flag.substring(2);
    }

    flagsAfterSplit = flag.split("=");
    for (int index = 0; index < flagsAfterSplit.length; index++) {
      String key = flagsAfterSplit[index];
      String value = null;
      ++index;
      int intValue = -1;

      if (index < flagsAfterSplit.length) {
        value = flagsAfterSplit[index];
        if (value.startsWith("$")
            || ((value.endsWith("h") || value.endsWith("H"))
                && ((value.matches("[0-9a-fA-F]+[hH]$"))))) {

          try {
            value = value.replace("$", "").replace("h", "").replace("H", "");
            intValue = Integer.parseInt(value, 16);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }

          addANewDefinition(key, intValue, null, DEFINITION_TYPE_VALUE);
        } else if (value.matches("[0-9]+")) {
          try {
            intValue = Integer.parseInt(value);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          addANewDefinition(key, intValue, null, DEFINITION_TYPE_VALUE);
        } else {
          addANewDefinition(key, 0, value, DEFINITION_TYPE_STRING);
        }
      } else {
        addANewDefinition(key, 0.0, null, DEFINITION_TYPE_VALUE);
      }
    }
  }

  public void redefine(
      String key, double doubleValue, String stringVal, Defines.DefinitionType type) {
    // Originally had two, now just have one
    addANewDefinition(key, doubleValue, stringVal, type);
  }

  public void addANewDefinition(
      String key, double doubleValue, String stringVal, Defines.DefinitionType type) {
    var definition = definitions.get(key);
    // Original code has a null check here, but this is Java we don't care about strings and memeory
    //        if (definition != null) {
    //            throw new RuntimeException(key + " already defined");
    //        }

    switch (type) {
      case DEFINITION_TYPE_VALUE:
      case DEFINITION_TYPE_STACK:
        definition = new Definition(doubleValue, type);
        break;
      case DEFINITION_TYPE_STRING:
      case DEFINITION_TYPE_ADDRESS_LABEL:
        definition = new Definition(stringVal, type);
        break;
    }

    definitions.put(key, definition);
  }

  public Map<String, Definition> getDefinitions() {
    return definitions;
  }

  public Defines.Output getOutputFormat() {
    return outputFormat;
  }

  public void setOutputFormat(Defines.Output outputFormat) {
    this.outputFormat = outputFormat;
  }

  public String getFinalName() {
    return finalName;
  }

  public String getExternalIncludesDirectory() {
    return extIncDir;
  }

  public boolean useExternalIncludesDirectory() {
    return useExtIncDir;
  }

  public boolean isListFileData() {
    return listFileData;
  }

  public boolean isVerboseMode() {
    return verboseMode;
  }

  public boolean isTestMode() {
    return testMode;
  }

  public boolean isMakefileRules() {
    return makefileRules;
  }

  public boolean isQuiet() {
    return quiet;
  }

  public boolean isExtraDefinitions() {
    return extraDefinitions;
  }

  public String getAsmName() {
    return asmName;
  }

  public enum Result {
    FAILED,
    SUCCEEDED
  }
}
