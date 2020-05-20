package org.abubusoft.mee.server.model;

import java.util.ArrayList;
import java.util.List;

public class CommandResponse {
  private final List<Double> values;
  private final CommandType commandType;
  private long responseTime;

  private CommandResponse(CommandType commandType, List<Double> values) {
    this.commandType = commandType;
    this.values = values;
  }

  public CommandType getCommandType() {
    return commandType;
  }

  public List<Double> getValues() {
    return values;
  }

  public long getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(long value) {
    this.responseTime = value;
  }

  public static class Builder {
    private CommandType commandType;
    private List<Double> values = new ArrayList<>();

    Builder(CommandType commandType) {
      this.commandType = commandType;
    }

    public static Builder create(CommandType commandType) {
      return new Builder(commandType);
    }

    public Builder addValue(double value) {
      values.add(value);
      return this;
    }

    public Builder addValue(CommandType commandType) {
      this.commandType = commandType;
      return this;
    }

    public CommandResponse build() {
      return new CommandResponse(commandType, values);
    }
  }
}
