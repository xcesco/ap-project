package cartesian;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class VariableInterval implements UniformDiscreetInterval<Double> {
  public Stream<Double> stream() {
    return DoubleStream.iterate(lowValue, v -> v <= highValue, value -> value + stepValue).boxed();
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

  public VariableInterval(String name, double lowValue, double stepValue, double highValue, long size) {
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
  private final long size;

  public long getSize() {
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

//        // step 0
//        AppAssert.assertTrue(
//                stepValue != 0, MalformedVariableDefinitionException.class, "definition of variable '%s' has step 0", name);
//
//        // step and interval are incosistent
//        AppAssert.assertTrue(
//                (lowerValue < highValue && stepValue > 0.0) || (lowerValue > highValue && stepValue < 0.0),
//                MalformedVariableDefinitionException.class,
//                "definition of variable '%s' is inconsistent", name);
//
//        // step is greater than interval
//        AppAssert.assertTrue(
//                Math.abs(stepValue) < Math.abs(highValue - lowerValue),
//                MalformedVariableDefinitionException.class,
//                "definition of variable '%s' has step greater than interval", name);

      return this;
    }

    public VariableInterval build() {
      double sizeToCheck = Math.floor(Math.abs(highValue - lowValue) / stepValue);
      assert sizeToCheck <= Long.MAX_VALUE;
      long size = (long) sizeToCheck + 1;

      return new VariableInterval(name, lowValue, stepValue, highValue, size);
    }
  }

}

