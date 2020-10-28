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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariablesDefinitionTest {

  @Test
  void testWrongDefinitions() {
    // step > 0
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(1, 0, 3);
    });

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(3, -1, 1);
    });

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(3, 1, 1);
    });


    assertEquals(1, builVariableDefinitions(1, 4, 3));
  }

  private int builVariableDefinitions(int i, int i2, int i3) {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(i, i2, i3).build());

    return variablesDefinition.getVariableValuesRange("x0").buildValuesList().size();
  }

  @Test
  void testGridValuesGeneration() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.GRID);

    assertEquals(values.size(), 100);
    assertEquals(values.get(0).getVariableValue("x0"), 1);
    assertEquals(values.get(0).getVariableValue("x1"), 11);
    assertEquals(values.get(99).getVariableValue("x0"), 10);
    assertEquals(values.get(99).getVariableValue("x1"), 20);
  }

  @Test
  void testListValuesGeneration() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.LIST);

    assertEquals(10, values.size());
    assertEquals(1, values.get(0).getVariableValue("x0"));
    assertEquals(11,values.get(0).getVariableValue("x1"));
    assertEquals(10,values.get(9).getVariableValue("x0"));
    assertEquals(20, values.get(9).getVariableValue("x1"));
  }

  @Test
  void testWrongListValues() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(1, 1, 20).build()
            );
    {
      List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.GRID);
      assertEquals(values.get(0).getVariableValue("x1"), 1);
    }
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> variablesDefinition.buildValues(ValueType.LIST));

  }
}