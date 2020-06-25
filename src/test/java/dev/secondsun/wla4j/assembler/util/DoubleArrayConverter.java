package dev.secondsun.wla4j.assembler.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

public class DoubleArrayConverter extends SimpleArgumentConverter {

  org.junit.platform.commons.JUnitException ex;

  @Override
  protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
    String[] doublesAsString = source.toString().replace("[", "").replace("]", "").split(",");

    if (!List.class.isAssignableFrom(targetType)) {
      throw new ArgumentConversionException("Must convert to a double array");
    }

    return Arrays.stream(doublesAsString).map(Double::parseDouble).collect(Collectors.toList());
  }
}
