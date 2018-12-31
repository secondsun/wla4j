package net.sagaoftherealms.tools.snes.assembler.pass.scan.token;

import java.io.Serializable;

public class Token implements Serializable {

  private final TokenTypes type;
  private final String string;
  private final String fileName;
  private final Position position;

  public Token(String tokenString, TokenTypes type, String fileName, Position position) {
    this.string = tokenString;
    this.type = type;
    this.fileName = fileName;
    this.position = position;
  }

  public TokenTypes getType() {
    return type;
  }

  public String getString() {
    return string;
  }

  @Override
  public String toString() {
    if (position != null) {
      return "Token{type="
          + type
          + ", string='"
          + string
          + '\''
          + "@"
          + fileName
          + ":"
          + position.beginLine
          + '}';
    }
    return "Token{type=" + type + ", string='" + string + "'}";
  }

  public String getFileName() {
    return fileName;
  }

  public Position getPosition() {
    return position;
  }

  public static class Position implements Serializable {
    public final int beginLine, beginOffset, endLine, endOffset;

    public Position(int beginLine, int beginPosition, int endLine, int endPosition) {
      this.beginLine = beginLine;
      this.beginOffset = beginPosition;
      this.endLine = endLine;
      this.endOffset = endPosition;
    }
  }
}
