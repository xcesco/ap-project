package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.*;
import org.abubusoft.mee.server.services.ExpressionEvaluator;

import java.util.List;
import java.util.StringJoiner;

public class ComputeCommand extends Command {
  private final ComputationType computationType;
  private final ValuesType valuesType;
  private final VariableDefinitions variableDefinitions;
  private final List<String> expressionsList;
  private final ExpressionEvaluator expressionEvaluator;

  public ComputeCommand(ComputationType computationType, ValuesType valuesType, VariableDefinitions variableDefinitions, List<String> expressionsList, ExpressionEvaluator expressionEvaluator) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valuesType = valuesType;
    this.variableDefinitions = variableDefinitions;
    this.expressionsList = expressionsList;
    this.expressionEvaluator = expressionEvaluator;
  }

  public ComputationType getComputationType() {
    return computationType;
  }

  public ValuesType getValuesType() {
    return valuesType;
  }

  public VariableDefinitions getVariableDefinitions() {
    return variableDefinitions;
  }

  public List<String> getExpressionsList() {
    return expressionsList;
  }

  public int getVariableDefinitionCount() {
    return variableDefinitions.size();
  }

  public List<String> getVariableNames() {
    return variableDefinitions.getKeysList();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ComputeCommand.class.getSimpleName() + "[", "]")
            .add("computationType=" + computationType)
            .add("valuesType=" + valuesType)
            .add("variableDefinitions=" + variableDefinitions)
            .add("expressionsList=" + expressionsList)
            .toString();
  }

  @Override
  public CommandResponse execute() {
    List<VariableValues> values = buildVariableValues();
    CommandResponse.Builder responseBuilder = CommandResponse.Builder.create(ResponseType.OK, getType());

    if (getComputationType() == ComputationType.COUNT) {
      return responseBuilder.addValue(values.size()).build();
    }

    try {
      double result = getInitialValue();
      for (String expression : expressionsList) {
        for (VariableValues value : values) {
          result = mergeResults(result, expressionEvaluator.execute(value, expression));
        }

        responseBuilder.addValue(finalizeResult(result, values.size()));
      }
    } catch (MalformedCommandException e) {
      e.printStackTrace();
    }

    return responseBuilder.build();
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

  private List<VariableValues> buildVariableValues() {
    return variableDefinitions.buildValues(this.valuesType);
  }

  public VariableDefinition getVariableDefinition(String variableName) {
    return variableDefinitions.get(variableName);
  }

  @Override
  public CommandResponse accept(CommandVisitor visitor) {
    return visitor.visit(this);
  }

  public static class Builder {
    private ComputationType computationType;
    private ValuesType valuesType;
    private VariableDefinitions variableDefinitions = new VariableDefinitions();
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

    public Builder setValuesType(ValuesType valuesType) {
      this.valuesType = valuesType;
      return this;
    }

    public ComputeCommand build() {
      return new ComputeCommand(computationType, valuesType, variableDefinitions, expressionsList, expressionEvaluator);
    }

    public Builder addVariableDefinition(VariableDefinition variableDefinition) {
      variableDefinitions.add(variableDefinition);
      return this;
    }

    public Builder setExpressionsList(List<String> expressionsList) {
      this.expressionsList = expressionsList;
      return this;
    }
  }
}
