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

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariableValuesRange {
  private final String name;
  private final double lowValue;
  private final double stepValue;
  private final double highValue;

  public VariableValuesRange(String name, double lowValue, double stepValue, double highValue) {
    this.name = name;
    this.lowValue = lowValue;
    this.stepValue = stepValue;
    this.highValue = highValue;
  }

  public List<Double> buildValuesList() {
    return IntStream.iterate(0, index -> lowValue + stepValue * index <= highValue, index -> index + 1)
            .mapToDouble(index -> lowValue + stepValue * index).boxed()
            .collect(Collectors.toList());
  }

  public String getName() {
    return name;
  }

  public static class Builder {
    private String name;
    private double lowValue;
    private double stepValue;
    private double highValue;

    public static Builder create() {
      return new Builder();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setInterval(double lower, double step, double high) {
      this.lowValue = lower;
      this.stepValue = step;
      this.highValue = high;

      // step > 0
      AppAssert.assertTrue(
              stepValue > 0, InvalidVariableDefinitionException.class, "Variable definition '%s' has step <=0", name);

      // step and interval are incosistent
      AppAssert.assertTrue(
              lowValue <= highValue,
              InvalidVariableDefinitionException.class,
              "Inconsistent variable definition '%s'", name);
      return this;
    }

    public VariableValuesRange build() {
      return new VariableValuesRange(name, lowValue, stepValue, highValue);
    }
  }

}

