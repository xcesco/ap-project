package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VariableValuesRange {
  private final String name;
  private final List<Double> values;

  public VariableValuesRange(String name, double lowValue, double stepValue, double highValue) {
    this.name = name;
    this.values = IntStream.iterate(0, index -> lowValue + stepValue * index <= highValue, index -> index + 1)
            .mapToDouble(index -> lowValue + stepValue * index).boxed()
            .collect(Collectors.toList());
  }

  public List<Double> getValues() {
    return values;
  }

  public String getName() {
    return name;
  }

  public Double get(int index) {
    return values.get(index);
  }

  public Double getLowValue() {
    return values.get(0);
  }

  public Double getHighValue() {
    return values.get(values.size()-1);
  }

  public int getSize() {
    return values.size();
  }

  public static class Builder {
    private String name;
    private double lowValue;
    private double stepValue;
    private double highValue;

    public static Builder create() {
      return new Builder();
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setInterval(double lower, double step, double high) {
      this.lowValue = lower;
      this.stepValue = step;
      this.highValue = high;

      // step > 0
      AppAssert.assertTrue(
              stepValue > 0, InvalidVariableDefinitionException.class, "Variable definition '%s' has step <=0", name);

      // step and interval are incosistent
      AppAssert.assertTrue(
              lowValue <= highValue,
              InvalidVariableDefinitionException.class,
              "Inconsistent variable definition '%s'", name);
      return this;
    }

    public VariableValuesRange build() {
      return new VariableValuesRange(name, lowValue, stepValue, highValue);
    }
  }

}
