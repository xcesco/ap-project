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

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.Expression;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.abubusoft.mee.server.support.ExpressionBuilderVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionBuilderVisitorTest {
  @Test
  public void testBuild() {
    String expressionString = "(x0+1)";
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(expressionString, CommandsParser::evaluate);
      MultiVariableValue multiVariableValue = MultiVariableValue.Builder
              .create()
              .add("x0", 1)
              .build();
      ExpressionBuilderVisitor visitor = new ExpressionBuilderVisitor(multiVariableValue, expressionString);
      ExpressionNode expressionNode = visitor.visit(parser);
      Expression expression = new Expression(expressionString, expressionNode);

      double value = expression.evaluate(multiVariableValue);
      assertEquals(2.0, value);
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }

  }
}
