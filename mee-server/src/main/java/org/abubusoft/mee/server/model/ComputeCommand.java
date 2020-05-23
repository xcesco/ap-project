package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
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

  public List<String> getExpressionsList() {
    return expressionsList;
  }

  @Override
  public CommandResponse execute() {
    List<VariableValues> values = buildVariableValues();
    CommandResponse.Builder responseBuilder = CommandResponse.Builder.ok();

    try {
      double result = getInitialValue();
      double actualValue;

      // validate with first variable values tuple
      for (String expression : expressionsList) {
        expressionEvaluator.validate(values.get(0), expression);
      }

      if (getComputationType() == ComputationType.COUNT) {
        return responseBuilder.setValue(values.size()).build();
      }

      for (String expression : expressionsList) {
        for (VariableValues value : values) {
          actualValue = expressionEvaluator.execute(value, expression);
          result = mergeResults(result, actualValue);
        }

        result = finalizeResult(result, values.size());
        logger.debug(String.format("%s_%s of '%s' (%s values) = %s",
                getComputationType(),
                getValuesType(),
                expression,
                values.size(),
                CommandResponseUtils.formatValue(result)));
        responseBuilder.setValue(result);

        // AVG require only 1st expression evaluation
        if (getComputationType() == ComputationType.AVG) {
          break;
        }
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
