package net.sagaoftherealms.tools.snes.assembler.util;

import java.io.Serializable;
import java.util.Objects;

/** This is a line in the InputData with metadata back to the original file. */
public class SourceDataLine implements Serializable {

  private String fileName = "";
  private int beginSourceLineNumber = 0;
  private int beginPosition = 0;
  
  private String dataLine = "";

  public SourceDataLine(String sourceFileName, int sourceLineNumber, String sourceCode) {
    fileName = sourceFileName;
    this.beginSourceLineNumber = sourceLineNumber;
    this.dataLine = sourceCode;
  }

  public SourceDataLine() {}

  /**
   * FileName is the original file name with the path.
   *
   * @return the filename with the path.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * FileName is the original file name with the path.
   *
   * @param fileName the filename with the path.
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * SourceLineNumber is the line in the original Source file
   *
   * @return the line of the original source file
   */
  public int getBeginSourceLineNumber() {
    return beginSourceLineNumber;
  }

  /**
   * SourceLineNumber is the line in the original Source file
   *
   * @param beginSourceLineNumber the line of the original source file
   */
  public void setBeginSourceLineNumber(int beginSourceLineNumber) {
    this.beginSourceLineNumber = beginSourceLineNumber;
  }

  /**
   * This is the actual source. The preprocessor removes comments, white space etc. Additionally the
   * system may expand macros and things from a single line of source from a source file. Therefore
   * multiple data lines may be generated from an original source line.
   *
   * @return the text of a line of data
   */
  public String getDataLine() {
    return dataLine;
  }

  /**
   * This is the actual source. The preprocessor removes comments, white space etc. Additionally the
   * system may expand macros and things from a single line of source from a source file. Therefore
   * multiple data lines may be generated from an original source line.
   *
   * @param dataLine the text of a line of data
   */
  public void setDataLine(String dataLine) {
    this.dataLine = dataLine;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SourceDataLine that = (SourceDataLine) o;
    return beginSourceLineNumber == that.beginSourceLineNumber
        && Objects.equals(fileName, that.fileName)
        && Objects.equals(dataLine, that.dataLine);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, beginSourceLineNumber, dataLine);
  }

  /**
   * Adds the character s to the line
   *
   * @param s character to add
   */
  public void append(char s) {
    dataLine += s;
  }

  /**
   * This will hash the file name. The reason is that in the original WLA tools each file had an id
   * which was used to find the file name of the section of bytecode in the wla tmp file. We don't
   * assign file names ids, so this is a workaround.
   *
   * @return the hash of the file name to be used as a integer lookup later
   */
  public int getFileId() {
    return fileName.hashCode();
  }

  public int getBeginPosition() {
    return beginPosition;
  }

  public void setBeginPosition(int beginPosition) {
    this.beginPosition = beginPosition;
  }

  @Override
  public String toString() {
    return "SourceDataLine{"
        + "fileName='"
        + fileName
        + '\''
        + ", beginSourceLineNumber="
        + beginSourceLineNumber
        + ", dataLine='"
        + dataLine
        + '\''
        + '}';
  }
}
