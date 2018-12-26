package net.sagaoftherealms.tools.snes.assembler.pass.scan.token;

import java.io.Serializable;
import net.sagaoftherealms.tools.snes.assembler.util.SourceDataLine;

public class Token implements Serializable {

  private final TokenTypes type;
  private final String string;
  private final SourceDataLine line;

  public Token(SourceDataLine line, String tokenString, TokenTypes type) {
    this.line = line;
    this.string = tokenString;
    this.type = type;
  }

  public TokenTypes getType() {
    return type;
  }

  public String getString() {
    return string;
  }

  @Override
  public String toString() {
    return "Token{type="
        + type
        + ", string='"
        + string
        + '\''
        + "@"
        + line.getFileName()
        + ":"
        + line.getSourceLineNumber()
        + '}';
  }
}
