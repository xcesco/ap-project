package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.AppAssert;
import org.abubusoft.mee.exceptions.MalformedVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;

public class VariableDefinition {
  public static class Builder {
    private String name;
    private List<Double> values;

    public static Builder create() {
      return new Builder();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setInterval(String lowerText, String stepText, String highText) {
      double lowerValue = Double.parseDouble(lowerText);
      double stepValue = Double.parseDouble(stepText);
      double highValue = Double.parseDouble(highText);

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
      return new VariableDefinition(this.name, this.values);
    }
  }

  private final String name;
  private final List<Double> values;

  public VariableDefinition(String name, List<Double> values) {
    this.name = name;
    this.values = values;
  }

  public String getName() {
    return name;
  }

  public List<Double> getValues() {
    return values;
  }


}
