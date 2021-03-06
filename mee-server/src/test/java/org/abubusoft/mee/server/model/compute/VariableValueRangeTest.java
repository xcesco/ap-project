/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.support.CommandVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableValueRangeTest {
  VariableValuesRange parseVariableDefinition(String variableName, String request) {
    ParserRuleContext parser = ParserRuleContextBuilder.build(request, CommandsParser::variable_values);
    CommandVisitor visitor = new CommandVisitor();
    visitor.visit(parser);
    ComputeCommand command = visitor.getComputeBuilder().build();
    return command.getVariableDefinition(variableName);
  }

  @Test
  void testVariableRange1_10() throws MalformedCommandException {
    String input = "x0:1:1:10";
    List<Double> list = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

    VariableValuesRange result = parseVariableDefinition("x0", input);

    assertEquals(list.size(), result.buildValuesList().size());
    assertEquals("x0", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.buildValuesList()));
  }

  @Test
  void testVariableRange0_1() throws MalformedCommandException {
    String input = "x0:0:0.1:1";
    List<Double> list = Arrays.asList(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    VariableValuesRange result = parseVariableDefinition("x0", input);

    assertEquals("x0", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.buildValuesList()));
  }

  @Test
  void testVariableRange2() throws MalformedCommandException {
    String input = "x0:-1:0.1:1";
    List<Double> list = Arrays.asList(-1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1,
            0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

    VariableValuesRange result = parseVariableDefinition("x0", input);

    assertEquals("x0", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.buildValuesList()));
  }

  @Test
  void testVariableRange3() throws MalformedCommandException {
    String input = "x1:-10:1:20";
    List<Double> list = Arrays.asList(-10.0, -9.0, -8.0, -7.0, -6.0, -5.0, -4.0, -3.0, -2.0, -1.0,
            0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0);

    VariableValuesRange result = parseVariableDefinition("x1", input);

    assertEquals("x1", result.getName());
    assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.buildValuesList()));
  }

  @Test
  void testVariableRange4() throws MalformedCommandException {
    checkRange("x1:0:2:9", 0, 8, 5);
    checkRange("x1:0:2:10", 0, 10, 6);
    checkRange("x1:0:1:10", 0, 10, 11);
    checkRange("x1:0:3:10", 0, 9, 4);
    checkRange("x1:0:.1:1", 0, 1, 11);
  }

  private VariableValuesRange checkRange(String input, double low, double high, int size) {
    VariableValuesRange result = parseVariableDefinition("x1", input);
    assertEquals("x1", result.getName());

    List<Double> values = result.buildValuesList();

    assertEquals(low, values.get(0));
    assertEquals(high, values.get(values.size() - 1));
    assertEquals(size, values.size());

    return result;
  }

  @Test
  void testExample() throws MalformedCommandException {
    String input = "x0:1:0.001:100";

    VariableValuesRange result = parseVariableDefinition("x0", input);

    assertEquals("x0", result.getName());
    //assertEquals(fixPrecisionOfListOfDouble(list), fixPrecisionOfListOfDouble(result.getValues()));
  }


  /**
   * 2.3.2.1 Parsing of VariabileValuesFunction - Error check
   */
  @Test
  void testInvalidDefinitions() {
    // range and step are incosistent
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> parseVariableDefinition("x0", "x0:1:0.1:-1"));
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> parseVariableDefinition("x0", "x0:-1:-0.1:1"));
    // step is 0
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> parseVariableDefinition("x0", "x0:-1:0:1"));
  }

  private List<String> fixPrecisionOfListOfDouble(List<Double> list) {
    return list.stream().map(item -> String.format("%.4f", item)).collect(Collectors.toList());
  }
}