package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.VariableDefinition;
import org.abubusoft.mee.server.services.impl.CommandParserImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariablesDefinitionParserImplTest {
  CommandParserImpl parser = new CommandParserImpl();

  @Test
  public void testVariableRange0_1() throws MalformedCommandException {
    String input = "x0:0:0.1:1";
    List<Double> list = Arrays.asList(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    VariableDefinition result = parser.parseVariableDefinition("x0", input);

    assertEquals("x0", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.getValues()));
  }

  @Test
  public void testVariableRange2() throws MalformedCommandException{
    String input = "x0:-1:0.1:1";
    List<Double> list = Arrays.asList(-1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1,
            0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    VariableDefinition result = parser.parseVariableDefinition("x0", input);

    assertEquals("x0", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.getValues()));
  }

  @Test
  public void testVariableRange3()throws MalformedCommandException {
    String input = "x1:-10:1:20";
    List<Double> list = Arrays.asList(-10.0, -9.0, -8.0, -7.0, -6.0, -5.0, -4.0, -3.0, -2.0, -1.0,
            0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0);

    VariableDefinition result = parser.parseVariableDefinition("x1", input);

    assertEquals("x1", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.getValues()));
  }

  /**
   * 2.3.2.1 Parsing of VariabileValuesFunction - Error check
   */
  @Test
  public void testInvalidDefinitions() {
    // range and step are incosistent
    Assertions.assertThrows(AppRuntimeException.class, () -> parser.parseVariableDefinition("x0", "x0 : 1 : 0.1 : -1"));
    Assertions.assertThrows(AppRuntimeException.class, () -> parser.parseVariableDefinition("x0","x0 :-1 :-0.1 :  1"));
    // step is 0
    Assertions.assertThrows(AppRuntimeException.class, () -> parser.parseVariableDefinition("x0","x0 :-1 : 0   :  1"));

    // step is greater than interval
    Assertions.assertThrows(AppRuntimeException.class, () -> parser.parseVariableDefinition("x0","x0 :-1 : 10  :  1"));
  }

  private List<String> fixPrecisionOfListOfDouble(List<Double> list) {
    return list.stream().map(item -> String.format("%.4f", item)).collect(Collectors.toList());
  }
}