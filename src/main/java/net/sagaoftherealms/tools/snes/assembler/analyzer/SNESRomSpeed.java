package net.sagaoftherealms.tools.snes.assembler.analyzer;

import java.util.List;
import java.util.stream.Collectors;

public enum SNESRomSpeed {
    SLOWROM, FASTROM;


    /**
     * Gets these values as a List of strings for text matching
     * @return a list of these values, stringified
     */
    public static List<String> asCollection() {
        return List.of(SNESRomSpeed.values())
                .stream()
                .map(SNESRomSpeed::name)
                .collect(Collectors.toList());
    }
}
