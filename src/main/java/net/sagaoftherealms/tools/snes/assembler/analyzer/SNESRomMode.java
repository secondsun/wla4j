package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum SNESRomMode {
  HIROM, EXHIROM, LOROM, EXLOROM;


  /**
   * Gets these values as a List of strings for text matching
   * @return a list of these values, stringified
   */
  public static List<String> asCollection() {
    return List.of(SNESRomMode.values())
        .stream()
        .map(SNESRomMode::name)
        .collect(Collectors.toList());
  }
}
