package org.abubusoft.mee.server.model;

import java.util.ArrayList;
import java.util.List;

public class CommandResponse {
  private final List<Double> values;
  private final CommandType commandType;
  private final ResponseType responseType;
  private long responseTime;

  private CommandResponse(ResponseType responseType, CommandType commandType, List<Double> values) {
    this.commandType = commandType;
    this.values = values;
    this.responseType = responseType;
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
    private final ResponseType responseType;
    private final CommandType commandType;
    private List<Double> values = new ArrayList<>();

    Builder(ResponseType responseType, CommandType commandType) {
      this.commandType = commandType;
      this.responseType = responseType;
    }

    public static Builder create(ResponseType responseType, CommandType commandType) {
      return new Builder(responseType, commandType);
    }

    public Builder addValue(double value) {
      values.add(value);
      return this;
    }

    public CommandResponse build() {
      return new CommandResponse(responseType, commandType, values);
    }
  }
}
