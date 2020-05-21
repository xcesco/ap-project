package org.abubusoft.mee.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CommandResponse {
  private final List<Double> values;
  private final ResponseType responseType;
  private final String message;
  private long responseTime;

  @Override
  public String toString() {
    return new StringJoiner(", ", CommandResponse.class.getSimpleName() + "[", "]")
            .add("values=" + values)
            .add("responseType=" + responseType)
            .add("message='" + message + "'")
            .add("responseTime=" + responseTime)
            .toString();
  }

  private CommandResponse(List<Double> values) {
    this.responseType = ResponseType.OK;
    this.values = values;
    this.message = null;
  }

  private CommandResponse(Exception exception) {
    this.responseType = ResponseType.ERR;
    this.values = null;
    this.message = String.format("(%s) %s", exception.getClass().getSimpleName(), exception.getMessage());
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

  public ResponseType getResponseType() {
    return this.responseType;
  }

  public static CommandResponse error(Exception exception) {
    return new CommandResponse(exception);
  }

  public String getMessage() {
    return this.message;
  }

  public static class Builder {
    private final ResponseType responseType;
    private List<Double> values = new ArrayList<>();

    Builder() {
      this.responseType = ResponseType.OK;
    }

    public static Builder ok() {
      return new Builder();
    }

    public Builder addValue(double value) {
      values.add(value);
      return this;
    }

    public CommandResponse build() {
      return new CommandResponse(values);
    }
  }
}
