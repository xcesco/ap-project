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

package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.model.compute.Expression;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionWithVariablesTest {
  @Test
  void testNumbersEvaluation() throws MalformedCommandException {
    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("x5", 1.0, 0, 1.0));
    evaluateExpression("x", 1.0, 0, 1.0);
    evaluateExpression("(0-x)", 2.0, 0, -2);
    evaluateExpression("(100*y1)", 0, 2, 200.0);
    evaluateExpression("(x+y1)", 1, 2, 3.0);
  }

  @Test
  void testNumbersSumSubEvaluation() throws MalformedCommandException {
    evaluateExpression("(x+1)", 1, 0, 2.0);
    evaluateExpression("(y1-1)", 0, 0, -1.0);
    evaluateExpression("(((x-1)+2)+y1)", 1, 2, 4.0);
  }

  @Test
  void testNumbersMulDivEvaluation() throws MalformedCommandException {
    evaluateExpression("(x*y1)", 2, 4, 8.0);
    evaluateExpression("(0-(x*2))", 4, 0, -8.0);
    evaluateExpression("(x/1)", 2, 0, 2.0);
    evaluateExpression("((8/x)/2)", 2, 0, 2.0);

    evaluateExpression("(2/x)", 0, 0, Double.POSITIVE_INFINITY);
  }

  @Test
  void testNumbers4OpsEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+(x*2))", 2, 0, 5.0);
    evaluateExpression("((1+y1)*2)", 0, 2, 6.0);
    evaluateExpression("(0-((x+y1)*2))", 1, 2, -6.0);
    evaluateExpression("((x-1)*y1)", 2, 3, 3.0);
  }

  @Test
  void testNumbersPowEvaluation() throws MalformedCommandException {
    evaluateExpression("(x^y1)", 2, 1, 2.0);
    evaluateExpression("((y1^x)^2)", 2, 2, 16.0);
    evaluateExpression("((2^y1)*y1)", 0, 2, 8.0);
  }

  @Test
  void testVariableUndefined() throws MalformedCommandException {
    Assertions.assertThrows(UndefinedVariableException.class, () -> evaluateExpression("(x^y2)", 2, 1, 2.0));
  }

  @Test
  void testMixEvaluation() throws MalformedCommandException {
    evaluateExpression("((1+x)^2)", 1, 1, 4.0);
    evaluateExpression("(((y1+1)^2)-1)", 1, 1, 3.0);
  }

  void evaluateExpression(String input, double variableValueX, double variableValueY1, double aspectedValue) throws MalformedCommandException {
    MultiVariableValue values = MultiVariableValue.Builder.create()
            .add("x", variableValueX)
            .add("y1", variableValueY1)
            .build();
    Expression expression = ComputeCommand.buildExpression(values, input);
    double evaluationResult = expression.evaluate(values);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}