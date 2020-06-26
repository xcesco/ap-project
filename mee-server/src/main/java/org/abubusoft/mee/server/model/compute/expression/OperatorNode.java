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

package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

import java.util.function.BinaryOperator;

public class OperatorNode implements ExpressionNode {
  private final ExpressionNode operandLeft;
  private final ExpressionNode operandRight;

  private final Type type;

  public OperatorNode(Type type, ExpressionNode operandLeft, ExpressionNode operandRight) {
    this.operandLeft = operandLeft;
    this.operandRight = operandRight;
    this.type = type;
  }

  @Override
  public double evaluate(MultiVariableValue multiVariableValue) {
    return type.function.apply(operandLeft.evaluate(multiVariableValue),
            operandRight.evaluate(multiVariableValue));
  }

  public enum Type {
    SUM(Double::sum),
    SUBTRACTION((a, b) -> a - b),
    MULTIPLICATION((a, b) -> a * b),
    DIVISION((a, b) -> a / b),
    POWER(Math::pow);

    private final BinaryOperator<Double> function;

    Type(BinaryOperator<Double> function) {
      this.function = function;
    }

  }
}
