package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.AppAssert;
import org.abubusoft.mee.exceptions.MalformedVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class VariableDefinition {
  private final String name;
  private final List<Double> values;
  private final double stepValue;

  @Override
  public String toString() {
    return new StringJoiner(", ", VariableDefinition.class.getSimpleName() + "[", "]")
            .add("name='" + name + "'")
            .add("values=" + values)
            .add("stepValue=" + stepValue)
            .toString();
  }

  public VariableDefinition(String name, double stepValue, List<Double> values) {
    this.name = name;
    this.values = values;
    this.stepValue = stepValue;
  }

  public String getName() {
    return name;
  }

  public List<Double> getValues() {
    return values;
  }

  public Double getLowerValue() {
    return values.get(0);
  }

  public double getStepValue() {
    return stepValue;
  }

  public Double getUpperValue() {
    return values.get(values.size() - 1);
  }

  public static class Builder {
    private String name;
    private List<Double> values;
    private double stepValue;

    public static Builder create() {
      return new Builder();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setInterval(double lowerValue, double stepValue, double highValue) {
      this.stepValue = stepValue;

      // step 0
      AppAssert.assertTrue(
              stepValue != 0, MalformedVariableDefinitionException.class, "definition of variable '%s' has step 0", name);

      // step and interval are incosistent
      AppAssert.assertTrue(
              (lowerValue < highValue && stepValue > 0.0) || (lowerValue > highValue && stepValue < 0.0),
              MalformedVariableDefinitionException.class,
              "definition of variable '%s' is inconsistent", name);

      // step is greater than interval
      AppAssert.assertTrue(
              Math.abs(stepValue) < Math.abs(highValue - lowerValue),
              MalformedVariableDefinitionException.class,
              "definition of variable '%s' has step greater than interval", name);

      values = new ArrayList<>();
      values.add(lowerValue);
      int size = (int) ((highValue - lowerValue) / stepValue);
      for (int i = 1; i < size; i++) {
        values.add(lowerValue + stepValue * i);
      }

      values.add(highValue);

      return this;
    }

    public VariableDefinition build() {
      return new VariableDefinition(this.name, this.stepValue, this.values);
    }
  }
}
