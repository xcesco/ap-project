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
import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.support.CommandResponseUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class MultiVariableValue {
  private final Map<String, Double> values;

  MultiVariableValue(Map<String, Double> values) {
    this.values = values;
  }

  public Double getVariableValue(String name) {
    return values.get(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "(", ")")
            .add(values.entrySet().stream()
                    .map(item -> item.getKey() + "=" + CommandResponseUtils.formatValue(item.getValue()))
                    .collect(Collectors.joining(", ")))
            .toString();
  }

  public static class Builder {
    /**
     * LinkedHashMap is used to respect insertion order
     */
    private final Map<String, Double> values = new LinkedHashMap<>();

    public static Builder create() {
      return new Builder();
    }

    public Builder add(String variableName, double variableValue) {
      values.put(variableName, variableValue);
      return this;
    }

    public Builder addAll(List<String> keysList, List<Double> valuesList) {
      AppAssert.assertTrue(keysList.size() == valuesList.size(), AppRuntimeException.class, "Invalid variable values definition");
      for (int i = 0; i < keysList.size(); i++) {
        add(keysList.get(i), valuesList.get(i));
      }
      return this;
    }

    public MultiVariableValue build() {
      return new MultiVariableValue(values);
    }
  }
}
