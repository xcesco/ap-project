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

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.*;
import org.abubusoft.mee.server.model.compute.expression.ExpressionNode;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.abubusoft.mee.server.support.ExpressionBuilderVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringJoiner;

public class ComputeCommand extends Command {
  private static final Logger logger = LoggerFactory
          .getLogger(ComputeCommand.class);
  private final ComputationType computationType;
  private final ValueType valueType;
  private final VariablesDefinition variablesDefinition;
  private final List<String> expressionsList;

  public ComputeCommand(ComputationType computationType, ValueType valueType, VariablesDefinition variablesDefinition, List<String> expressionsList) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valueType = valueType;
    this.variablesDefinition = variablesDefinition;
    this.expressionsList = expressionsList;
  }

  static Expression buildExpression(MultiVariableValue values, String expressionString) {
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(expressionString, CommandsParser::evaluate);
      ExpressionBuilderVisitor visitor = new ExpressionBuilderVisitor(values, expressionString);
      ExpressionNode treeNodeRoot = visitor.visit(parser);
      return new Expression(expressionString, treeNodeRoot);
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }
  }

  public ComputationType getComputationType() {
    return computationType;
  }

  public ValueType getValueType() {
    return valueType;
  }

  public List<String> getExpressionsList() {
    return expressionsList;
  }

  @Override
  public CommandResponse execute() {
    List<MultiVariableValue> values = buildMultiVariableValues();
    CommandResponse.Builder responseBuilder = CommandResponse.Builder.ok();

    double currentResult = getInitialValue();

    switch (getComputationType()) {
      case COUNT:
        currentResult = values.size();
        break;
      case AVG:
        String firstExpression = expressionsList.get(0);
        currentResult = evaluateExpression(values, currentResult, firstExpression);
        break;
      case MAX:
      case MIN:
        for (String expression : expressionsList) {
          currentResult = evaluateExpression(values, currentResult, expression);
        }
        break;
      default:
        AppAssert.fail(AppRuntimeException.class, "Unsupported computation type '%s'", getComputationType());
        break;
    }
    if (logger.isDebugEnabled()) {
      StringJoiner joiner = new StringJoiner(",","[", "]");
      expressionsList.forEach(joiner::add);
      logger.debug(String.format("%s_%s of %s (on %s values) = %s", getComputationType(), getValueType(), joiner.toString(), values.size(), CommandResponseUtils.formatValue(currentResult)));
    }
    responseBuilder.setValue(currentResult);

    return responseBuilder.build();
  }

  private double evaluateExpression(List<MultiVariableValue> values, double result, String expressionString) {
    double actualValue;
    boolean trace = logger.isTraceEnabled();

    Expression expression = buildExpression(values.get(0), expressionString);
    if (trace) {
      logger.trace("Begin '{}' evaluation", expression);
    }
    for (MultiVariableValue value : values) {
      actualValue = expression.evaluate(value);
      if (trace) {
        logger.trace("'{}' with {} = {}", expression, value, CommandResponseUtils.formatValue(actualValue));
      }
      result = mergeResults(result, actualValue);
    }

    result = finalizeResult(result, values.size());

    if (trace) {
      logger.trace("End '{}' evaluation with temporary result = {}", expression, CommandResponseUtils.formatValue(result));
    }
    return result;
  }

  private double finalizeResult(double temporaryResult, int valuesCount) {
    switch (getComputationType()) {
      case AVG:
        return temporaryResult / valuesCount;
      case MIN:
      case MAX:
      case COUNT:
      default:
        return temporaryResult;
    }
  }

  private double mergeResults(double temporaryResult, double actualResult) {
    switch (getComputationType()) {
      case MIN:
        return Math.min(temporaryResult, actualResult);
      case AVG:
        return temporaryResult + actualResult;
      case MAX:
        return Math.max(temporaryResult, actualResult);
      case COUNT:
      default:
        return 0;
    }
  }

  private double getInitialValue() {
    switch (getComputationType()) {
      case MIN:
        return Double.MAX_VALUE;
      case MAX:
        return Double.MIN_VALUE;
      case AVG:
      case COUNT:
      default:
        return 0;
    }
  }

  private List<MultiVariableValue> buildMultiVariableValues() {
    return variablesDefinition.buildValues(valueType);
  }

  public VariableValuesRange getVariableDefinition(String variableName) {
    return variablesDefinition.getVariableValuesRange(variableName);
  }

  @Override
  public CommandResponse accept(CommandVisitor visitor) {
    return visitor.visit(this);
  }

  public static class Builder {
    private final VariablesDefinition variablesDefinition = new VariablesDefinition();
    private ComputationType computationType;
    private ValueType valueType;
    private List<String> expressionsList;

    private Builder() {

    }

    public static Builder create() {
      return new Builder();
    }

    public Builder setComputationType(ComputationType computationType) {
      this.computationType = computationType;
      return this;
    }

    public Builder setValueType(ValueType valueType) {
      this.valueType = valueType;
      return this;
    }

    public ComputeCommand build() {
      return new ComputeCommand(computationType, valueType, variablesDefinition, expressionsList);
    }

    public Builder addVariableDefinition(VariableValuesRange variableValuesRange) {
      variablesDefinition.add(variableValuesRange);
      return this;
    }

    public Builder setExpressionsList(List<String> expressionsList) {
      this.expressionsList = expressionsList;
      return this;
    }
  }
}
