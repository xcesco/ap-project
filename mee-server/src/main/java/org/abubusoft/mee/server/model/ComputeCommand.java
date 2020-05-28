package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.model.compute.*;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComputeCommand extends Command {
  private static final Logger logger = LoggerFactory
          .getLogger(ComputeCommand.class);
  private final ComputationType computationType;
  private final ValueType valueType;
  private final VariablesDefinition variablesDefinition;
  private final List<String> expressionsList;
  private final ExpressionEvaluator expressionEvaluator;

  public ComputeCommand(ComputationType computationType, ValueType valueType, VariablesDefinition variablesDefinition, List<String> expressionsList, ExpressionEvaluator expressionEvaluator) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valueType = valueType;
    this.variablesDefinition = variablesDefinition;
    this.expressionsList = expressionsList;
    this.expressionEvaluator = expressionEvaluator;
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
        currentResult = evaluateSingleExpression(values, currentResult, firstExpression);
        break;
      case MAX:
      case MIN:
        for (String expression : expressionsList) {
          currentResult = evaluateSingleExpression(values, currentResult, expression);
        }
        break;
      default:
        AppAssert.fail(AppRuntimeException.class, "Unsupported computation type '%s'", getComputationType());
        break;
    }
    responseBuilder.setValue(currentResult);

    return responseBuilder.build();
  }

  private double evaluateSingleExpression(List<MultiVariableValue> values, double result, String expression) {
    double actualValue;
    for (MultiVariableValue value : values) {
      actualValue = expressionEvaluator.evaluate(value, expression);
      result = mergeResults(result, actualValue);
    }

    result = finalizeResult(result, values.size());
    logger.debug(String.format("%s_%s of '%s' (on %s values) = %s", getComputationType(), getValueType(), expression, values.size(), CommandResponseUtils.formatValue(result)));
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

  public VariableTuple getVariableDefinition(String variableName) {
    return variablesDefinition.get(variableName);
  }

  @Override
  public CommandResponse accept(CommandVisitor visitor) {
    return visitor.visit(this);
  }

  public static class Builder {
    private ComputationType computationType;
    private ValueType valueType;
    private final VariablesDefinition variablesDefinition = new VariablesDefinition();
    private List<String> expressionsList;
    private final ExpressionEvaluator expressionEvaluator;

    public Builder(ExpressionEvaluator expressionEvaluator) {
      this.expressionEvaluator = expressionEvaluator;
    }

    public static Builder create(ExpressionEvaluator expressionEvaluator) {
      return new Builder(expressionEvaluator);
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
      return new ComputeCommand(computationType, valueType, variablesDefinition, expressionsList, expressionEvaluator);
    }

    public Builder addVariableDefinition(VariableTuple variableTuple) {
      variablesDefinition.add(variableTuple);
      return this;
    }

    public Builder setExpressionsList(List<String> expressionsList) {
      this.expressionsList = expressionsList;
      return this;
    }
  }
}
