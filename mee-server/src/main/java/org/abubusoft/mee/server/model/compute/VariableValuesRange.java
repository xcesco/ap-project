package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VariableValuesRange {
  public Stream<Double> getValuesStream() {
    return IntStream.range(0, size).mapToDouble(index -> lowValue + index * stepValue).boxed();
  }

  public List<Double> getValues() {
    return getValuesStream().collect(Collectors.toList());
  }

  public String getName() {
    return name;
  }

  public Double get(long index) {
    return lowValue + stepValue * index;
  }

  public Double getLowValue() {
    return lowValue;
  }

  public Double getStepValue() {
    return stepValue;
  }

  public Double getHighValue() {
    return highValue;
  }

  public VariableValuesRange(String name, double lowValue, double stepValue, double highValue, int size) {
    this.name = name;
    this.lowValue = lowValue;
    this.stepValue = stepValue;
    this.highValue = highValue;
    this.size = size;
  }

  private final String name;
  private final double lowValue;
  private final double stepValue;
  private final double highValue;
  private final int size;

  public int getSize() {
    return size;
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

      // step 0
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
      double sizeToCheck = Math.floor(Math.abs(highValue - lowValue) / stepValue);
      assert sizeToCheck <= Long.MAX_VALUE;
      int size = (int) sizeToCheck + 1;

      return new VariableValuesRange(name, lowValue, stepValue, highValue, size);
    }
  }

}

