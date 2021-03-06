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

import com.google.common.collect.Lists;
import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VariablesDefinition {
  private final List<VariableValuesRange> variableValuesRanges;

  public VariablesDefinition() {
    this.variableValuesRanges = new ArrayList<>();
  }

  public List<String> getVariableNameList() {
    return variableValuesRanges.stream().map(VariableValuesRange::getName).collect(Collectors.toList());
  }

  private boolean isAlreadyDefined(String name) {
    return variableValuesRanges.stream()
            .anyMatch(item -> item.getName().equals(name));
  }

  public VariablesDefinition add(VariableValuesRange variableValuesList) {
    if (isAlreadyDefined(variableValuesList.getName())) {
      String message = String.format("Variable '%s' is defined twice", variableValuesList.getName());
      AppAssert.fail(InvalidVariableDefinitionException.class, message);
    }
    variableValuesRanges.add(variableValuesList);
    return this;
  }

  public List<MultiVariableValue> buildValues(ValueType valueType) {
    return buildValuesAsStream(valueType).collect(Collectors.toList());
  }

  public Stream<MultiVariableValue> buildValuesAsStream(ValueType valueType) {
    final List<String> variableNames = getVariableNameList();
    final List<List<Double>> values = variableValuesRanges.stream()
            .map(VariableValuesRange::buildValuesList)
            .collect(Collectors.toList());

    if (valueType == ValueType.GRID) {
      return Lists.cartesianProduct(values)
              .stream()
              .map(value -> MultiVariableValue.Builder.create()
                      .addAll(variableNames, value).build());
    } else {
      checkVariableRangeSize(values);
      final int rangeSize = values.get(0).size();
      final int variableCount = variableValuesRanges.size();

      Stream<List<Double>> stream = IntStream.range(0, rangeSize)
              .mapToObj(valueIndex -> IntStream.range(0, variableCount)
                      .mapToObj(variableIndex -> values.get(variableIndex).get(valueIndex))
                      .collect(Collectors.toList()));

      return stream.map(value -> MultiVariableValue.Builder.create().addAll(variableNames, value).build());
    }
  }

  private void checkVariableRangeSize(List<List<Double>> values) {
    final int firstCount = values.get(0).size();
    final String firstName = variableValuesRanges.get(0).getName();
    IntStream.range(0, variableValuesRanges.size()).forEach(index -> {
      int currentCount = values.get(index).size();
      String currentName = variableValuesRanges.get(index).getName();
      AppAssert.assertTrue(firstCount == currentCount, InvalidVariableDefinitionException.class,
              "Variables '%s' and '%s' have different values range size (%s, %s)",
              firstName, currentName, firstCount, currentCount);
    });
  }

  public VariableValuesRange getVariableValuesRange(String variableName) {
    return this.variableValuesRanges.stream().filter(item -> item.getName().equals(variableName)).findFirst().orElse(null);
  }
}
