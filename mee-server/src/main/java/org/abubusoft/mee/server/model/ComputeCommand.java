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
    List<MultiVariableValue> values = buildVariableValues();
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
      StringJoiner joiner = new StringJoiner(",");
      expressionsList.forEach(joiner::add);
      logger.debug(String.format("%s_%s of '%s' (on %s values) = %s", getComputationType(), getValueType(), joiner.toString(), values.size(), CommandResponseUtils.formatValue(currentResult)));
    }
    responseBuilder.setValue(currentResult);

    return responseBuilder.build();
  }

  private double evaluateExpression(List<MultiVariableValue> values, double result, String expressionString) {
    double actualValue;
    boolean trace = logger.isTraceEnabled();

    Expression expression = buildExpression(values.get(0), expressionString);
    if (trace) {
      logger.trace(String.format("Begin '%s' evaluation with partial result = %s", expression, CommandResponseUtils.formatValue(result)));
    }
    for (MultiVariableValue value : values) {
      actualValue = expression.evaluate(value);
      if (trace) {
        logger.trace(String.format("'%s' with %s = %s", expression, value, CommandResponseUtils.formatValue(actualValue)));
      }
      result = mergeResults(result, actualValue);
    }

    result = finalizeResult(result, values.size());

    if (trace) {
      logger.trace(String.format("End '%s' evaluation with partial result = %s", expression, CommandResponseUtils.formatValue(result)));
    }
    return result;
  }

  private double finalizeResult(double result, int size) {
    switch (getComputationType()) {
      case AVG:
        return result / size;
      case MIN:
      case MAX:
      case COUNT:
      default:
        return result;
    }
  }

  private double mergeResults(double result, double actualResult) {
    switch (getComputationType()) {
      case MIN:
        return Math.min(result, actualResult);
      case AVG:
        return result + actualResult;
      case MAX:
        return Math.max(result, actualResult);
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

  private List<MultiVariableValue> buildVariableValues() {
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
