package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.model.compute.*;
import org.abubusoft.mee.server.services.ExpressionEvaluatorService;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ComputeCommand extends Command {
  private static final Logger logger = LoggerFactory
          .getLogger(ComputeCommand.class);
  private final ComputationType computationType;
  private final ValuesType valuesType;
  private final VariablesDefinition variablesDefinition;
  private final List<String> expressionsList;
  private final ExpressionEvaluatorService expressionEvaluatorService;

  public ComputeCommand(ComputationType computationType, ValuesType valuesType, VariablesDefinition variablesDefinition, List<String> expressionsList, ExpressionEvaluatorService expressionEvaluatorService) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valuesType = valuesType;
    this.variablesDefinition = variablesDefinition;
    this.expressionsList = expressionsList;
    this.expressionEvaluatorService = expressionEvaluatorService;
  }

  public ComputationType getComputationType() {
    return computationType;
  }

  public ValuesType getValuesType() {
    return valuesType;
  }

  public List<String> getExpressionsList() {
    return expressionsList;
  }

  @Override
  public CommandResponse execute() {
    List<VariablesValue> values = buildVariableValues();
    CommandResponse.Builder responseBuilder = CommandResponse.Builder.ok();

    double result = getInitialValue();
    double actualValue;

    if (getComputationType() == ComputationType.COUNT) {
      return responseBuilder.setValue(values.size()).build();
    }

    for (String expression : expressionsList) {
      // validate expression once, with first available var values
      expressionEvaluatorService.checkVariablesInExpression(values.get(0), expression);

      for (VariablesValue value : values) {
        actualValue = expressionEvaluatorService.evaluate(value, expression);
        result = mergeResults(result, actualValue);
      }

      result = finalizeResult(result, values.size());
      logger.debug(String.format("%s_%s of '%s' (%s values) = %s",
              getComputationType(),
              getValuesType(),
              expression,
              values.size(),
              CommandResponseUtils.formatValue(result)));

      // AVG require only 1st expression evaluation
      if (getComputationType() == ComputationType.AVG) {
        break;
      }
    }

    responseBuilder.setValue(result);

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

  private List<VariablesValue> buildVariableValues() {
    return variablesDefinition.buildValues(valuesType);
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
    private ValuesType valuesType;
    private VariablesDefinition variablesDefinition = new VariablesDefinition();
    private List<String> expressionsList;
    private final ExpressionEvaluatorService expressionEvaluatorService;

    public Builder(ExpressionEvaluatorService expressionEvaluatorService) {
      this.expressionEvaluatorService = expressionEvaluatorService;
    }

    public static Builder create(ExpressionEvaluatorService expressionEvaluatorService) {
      return new Builder(expressionEvaluatorService);
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
      return new ComputeCommand(computationType, valuesType, variablesDefinition, expressionsList, expressionEvaluatorService);
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
