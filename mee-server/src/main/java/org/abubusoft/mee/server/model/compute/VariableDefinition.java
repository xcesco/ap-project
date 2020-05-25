package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;

public class VariableDefinition {
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

    public Builder setInterval(double lowerValue, double stepValue, double higherValue) {

      try {
        // step 0
        AppAssert.assertTrue(
                stepValue > 0, InvalidVariableDefinitionException.class, "Variable definition '%s' has step <=0", name);

        // step and interval are incosistent
        AppAssert.assertTrue(
                lowerValue < higherValue,
                InvalidVariableDefinitionException.class,
                "Definition of variable '%s' is inconsistent", name);

        // step is greater than interval
        AppAssert.assertTrue(
                stepValue < Math.abs(higherValue - lowerValue),
                InvalidVariableDefinitionException.class,
                "Definition of variable '%s' has step greater than interval", name);

        values = new ArrayList<>();
        double value = lowerValue;

        for (int i = 1; value <= higherValue; i++) {
          values.add(value);
          value = lowerValue + stepValue * i;
        }

        return this;
      } catch (InvalidVariableDefinitionException e) {
        throw e;
      }
    }

    public VariableDefinition build() {
      return new VariableDefinition(this.name, this.values);
    }
  }
}
