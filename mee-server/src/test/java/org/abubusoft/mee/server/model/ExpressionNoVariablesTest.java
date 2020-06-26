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
import org.abubusoft.mee.server.model.compute.Expression;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionNoVariablesTest {

  @Test
  public void testNumbersEvaluation() throws MalformedCommandException {
    evaluateExpression("1", 1.0);
    evaluateExpression("(0-2)", -2.0);
    evaluateExpression("100.0", 100.0);
    evaluateExpression("100", 100.0);
    evaluateExpression("0.01", .01);
    evaluateExpression("0.001", .001);
    evaluateExpression("(0-0.001)", -.001);
    evaluateExpression("20", 20.0);
    evaluateExpression("200", 200);
  }

  @Test
  public void testNumbersSumSubEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+1)", 2.0);
    evaluateExpression("(1-1)", .0);
    evaluateExpression("((1-1)+2)", 2.0);
  }

  @Test
  public void testNumbersMulDivEvaluation() throws MalformedCommandException {
    evaluateExpression("(1*1)", 1.0);
    evaluateExpression("((0-0)-(1*1))", -1.0);
    evaluateExpression("(2/1)", 2.0);
    evaluateExpression("((8/2)/2)", 2.0);

    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("2/0", 2.0));
  }

  @Test
  public void testNumbers4OpsEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+(2*2))", 5.0);
    evaluateExpression("((1+2)*2)", 6.0);
    evaluateExpression("(0-((1+2)*2))", -6.0);
    evaluateExpression("((2-1)*2)", 2.0);
  }

  @Test
  public void testNumbersPowEvaluation() throws MalformedCommandException {
    evaluateExpression("(2^1)", 2.0);
    evaluateExpression("(2^(2^2))", 16.0);
    evaluateExpression("((2^2)*2)", 8.0);
  }

  @Test
  public void testMixEvaluation() throws MalformedCommandException {
    evaluateExpression("((1+1)^2)", 4.0);
    evaluateExpression("(((1+1)^2)-1)", 3.0);
  }

  @Test
  public void testMalformedExpression() {
    Assertions.assertThrows(MalformedCommandException.class, () -> evaluateExpression("(1+", 4.0));
    Assertions.assertThrows(MalformedCommandException.class, () -> evaluateExpression("(1/0", 4.0));
  }

  private void evaluateExpression(String input, double aspectedValue) {
    MultiVariableValue noValues = MultiVariableValue.Builder.create().build();
    Expression expression = ComputeCommand.buildExpression(noValues, input);
    double evaluationResult = expression.evaluate(noValues);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}