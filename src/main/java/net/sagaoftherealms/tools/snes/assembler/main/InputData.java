package net.sagaoftherealms.tools.snes.assembler.main;

import static net.sagaoftherealms.tools.snes.assembler.Defines.DefinitionType.DEFINITION_TYPE_STRING;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sagaoftherealms.tools.snes.assembler.definition.opcodes.OpCode;
import net.sagaoftherealms.tools.snes.assembler.util.SourceFileDataMap;
import net.sagaoftherealms.tools.snes.assembler.util.SourceScanner;
import org.apache.commons.io.IOUtils;

/**
 * This class is the "object" which is all of the input files. It is mutable and has a few
 * convenience functions for the parsers.
 */
public class InputData {

  final Flags flags;
  private String defaultIncludeDirectory = "." + File.pathSeparator;

  private SourceFileDataMap combinedSourceFile = new SourceFileDataMap();

  public InputData(Flags flags) {
    this.flags = flags;
  }

  public void includeFile(InputStream fileStream, String fileName, int includeAt) {

    if (flags.isExtraDefinitions()) {
      flags.redefine("WLA_FILENAME", 0.0, fileName, DEFINITION_TYPE_STRING);
      flags.redefine("wla_filename", 0.0, fileName, DEFINITION_TYPE_STRING);
    }

    String fileContents = null;

    try {
      fileContents = IOUtils.toString(fileStream, "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    /* preprocess */
    SourceFileDataMap preprocessedDataMap = preprocess_file(fileContents, fileName);
    combinedSourceFile.addMapAt(preprocessedDataMap, includeAt);
  }

  public void includeFile(String name) throws IOException {

    File f;

    String includeDirectory, fullName;

    if (name == null) {
      name = "";
    }

    /* create the full output file name */
    if (flags.useExternalIncludesDirectory()) {
      includeDirectory = flags.getExternalIncludesDirectory();
    } else {
      includeDirectory = defaultIncludeDirectory;
    }

    fullName = createFullName(includeDirectory, name);

    f = new File(fullName);

    if (!f.isFile() || !f.exists()) {

      throw new RuntimeException(String.format("Error opening file \"%s\".\n", fullName));
    }

    includeFile(new FileInputStream(f), fullName, 0);
  }

  private String createFullName(String path, String fileName) {
    return path + fileName;
  }

  /* the mystery preprocessor - touch it and prepare for trouble ;) the preprocessor
  removes as much white space as possible from the source file. this is to make
  the parsing of the file, that follows, simpler. */
  private SourceFileDataMap preprocess_file(String inputString, String file_name) {

    SourceFileDataMap buffer = new SourceFileDataMap();

    // We're going to try and keep the lines in sync so the source file and the preprocessed text
    // have a link.
    int sourceFileLine = 1;

    /* this is set to 1 when the parser finds a non white space symbol on the line it's parsing */
    int got_chars_on_line = 0;

    /* values for z - z tells us the state of the preprocessor on the line it is processing
    the value of z is 0 at the beginning of a new line, and can only grow: 0 -> 1 -> 2 -> 3
    0 - new line
    1 - 1+ characters on the line
    2 - extra white space removed
    3 - again 1+ characters follow */
    int z = 0;

    int square_bracket_open = 0;

    char inputArray[] = inputString.toCharArray();
    int input_end = inputArray.length;

    buffer.addLine(file_name, sourceFileLine, ""); // Start on Line one

    for (int input = 0; input < inputArray.length; ) {
      char inputTest = inputArray[input];
      switch (inputTest) {
        case ';':
          /* clear a commented line */
          input++;
          for (;
              input < input_end && inputArray[input] != 0x0A && inputArray[input] != 0x0D;
              input++) {}

          break;
        case '*':
          if (got_chars_on_line == 0) {
            /* clear a commented line */
            for (;
                input < input_end && inputArray[input] != 0x0A && inputArray[input] != 0x0D;
                input++) {}

          } else {
            /* multiplication! */
            input++;
            buffer.append('*');
          }
          break;
        case '/':
          if (inputArray[input + 1] == '*') {
            /* remove an ANSI C -style block comment */
            got_chars_on_line = 0;
            input += 2;
            while (got_chars_on_line == 0) {
              for (;
                  input < input_end && inputArray[input] != '/' && inputArray[input] != 0x0A;
                  input++) {}

              if (input >= input_end) {
                throw new RuntimeException(
                    String.format(
                        "Comment wasn't terminated properly in file \"%s\".\n", file_name));
              }
              if (inputArray[input] == 0x0A) {
                buffer.append((char) 0x0A);

                buffer.addLine(file_name, ++sourceFileLine, "");
              }

              if (inputArray[input] == '/' && inputArray[input - 1] == '*') {
                got_chars_on_line = 1;
              }
              input++;
            }

          } else {
            input++;
            buffer.append('/');
            got_chars_on_line = 1;
          }
          break;
        case ':':
          /* finding a label resets the counters */
          input++;
          buffer.append(':');
          got_chars_on_line = 0;
          break;
        case 0x09:
        case ' ':
          /* remove extra white space */
          input++;
          buffer.append(' ');

          for (;
              input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09);
              input++) {}

          got_chars_on_line = 1;
          if (z == 1) {
            z = 2;
          }
          break;
        case 0x0A:
          /* take away white space from the end of the line */
          input++;
          buffer.addLine(file_name, ++sourceFileLine, "");

          /* moving on to a new line */
          got_chars_on_line = 0;
          z = 0;
          square_bracket_open = 0;

          break;
        case 0x0D:
          input++;
          break;
        case '\'':
          if (inputArray[input + 2] == '\'') {
            buffer.append('\'');
            input++;
            buffer.append(inputArray[input]);

            input++;

            buffer.append('\'');
            input++;

          } else {
            buffer.append('\'');
            input++;
          }
          got_chars_on_line = 1;
          break;
        case '"':
          /* don't touch strings */
          buffer.append('"');
          input++;

          got_chars_on_line = 1;
          while (true) {
            for (;
                input < input_end
                    && inputArray[input] != '"'
                    && inputArray[input] != 0x0A
                    && inputArray[input] != 0x0D; ) {
              buffer.append(inputArray[input]);
              input++;
            }

            if (input >= input_end) {
              break;
            } else if (inputArray[input] == 0x0A || inputArray[input] == 0x0D) {
              /* process 0x0A/0x0D as usual, and later when we try to input a string, the parser will fail as 0x0A comes before a " */
              break;
            } else if (inputArray[input] == '"' && inputArray[input - 1] != '\\') {
              buffer.append('"');
              input++;

              break;
            } else {
              buffer.append('"');
              input++;
            }
          }
          break;

        case '(':
          buffer.append('(');
          input++;

          for (;
              input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09);
              input++) {}
          got_chars_on_line = 1;
          break;

        case ')':
          buffer.append(')');
          input++;
          got_chars_on_line = 1;
          break;

        case '[':
          buffer.append(inputArray[input]);
          input++;
          got_chars_on_line = 1;
          square_bracket_open = 1;
          break;

        case ',':
        case '+':
        case '-':
          if (got_chars_on_line == 0) {
            for (;
                input < input_end && (inputArray[input] == '+' || inputArray[input] == '-');
                input++) {
              buffer.append(inputArray[input]);
            }
            got_chars_on_line = 1;
          } else {

            buffer.append(inputArray[input]);
            input++;
            for (;
                input < input_end && (inputArray[input] == ' ' || inputArray[input] == 0x09);
                input++) {}
            got_chars_on_line = 1;
          }
          break;
        default:
          buffer.append(inputArray[input]);
          input++;

          got_chars_on_line = 1;

          /* mode changes... */
          if (z == 0) {
            z = 1;
          } else if (z == 2) {
            z = 3;
          }
          break;
      }
    }

    buffer.compress();

    return buffer;
  }

  /** Pretty prints the processed source */
  public String prettyPrint() {
    return combinedSourceFile.toString();
  }

  /**
   * Creates a scanner that uses a provide table of opcodes for the OpCode token type.
   *
   * @param opTable Array of opcodes
   * @return a scanner which tokenizes
   */
  public SourceScanner startRead(OpCode[] opTable) {
    return new SourceScanner(combinedSourceFile, opTable);
  }
}
