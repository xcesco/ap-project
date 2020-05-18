package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.CommandType;

public class ComputeCommand extends AbstractCommand {
  private final ComputationType computationType;
  private final ValuesType valuesType;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ComputeCommand{");
    sb.append("computationType=").append(computationType);
    sb.append(", valuesType=").append(valuesType);
    sb.append('}');
    return sb.toString();
  }

  public ComputeCommand(ComputationType computationType, ValuesType valuesType) {
    super(CommandType.COMPUTE);
    this.computationType = computationType;
    this.valuesType = valuesType;
  }

  public ComputationType getComputationType() {
    return computationType;
  }

  public ValuesType getValuesType() {
    return valuesType;
  }

  @Override
  public void execute() {

  }

  public static class Builder {
    private ComputationType computationType;
    private ValuesType valuesType;

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
      return new ComputeCommand(computationType, valuesType);
    }
  }
}
