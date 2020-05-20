package org.abubusoft.mee.server.model.commands;

import org.abubusoft.mee.server.model.*;

import java.util.List;
import java.util.StringJoiner;

public class ComputeCommand extends Command {
  private final ComputationType computationType;
  private final ValuesType valuesType;
  private final VariableDefinitions variableDefinitions;
  private final List<String> expressionsList;

  public ComputeCommand(ComputationType computationType, ValuesType valuesType, VariableDefinitions variableDefinitions, List<String> expressionsList) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valuesType = valuesType;
    this.variableDefinitions = variableDefinitions;
    this.expressionsList = expressionsList;
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

    switch (getComputationType()) {
      case MIN:
      case AVG:
      case MAX:
      case COUNT:
      default:
        return CommandResponse.Builder.create(getType()).build();
    }
  }

  private List<VariableValues> buildVariableValues() {
    return variableDefinitions.buildValues(this.valuesType);
  }

  public VariableDefinition getVariableDefinition(String variableName) {
    return variableDefinitions.get(variableName);
  }

  public static class Builder {
    private ComputationType computationType;
    private ValuesType valuesType;
    private VariableDefinitions variableDefinitions = new VariableDefinitions();
    private List<String> expressionsList;

    public static Builder create() {
      return new Builder();
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
      return new ComputeCommand(computationType, valuesType, variableDefinitions, expressionsList);
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
