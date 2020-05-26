package org.abubusoft.mee.server.model;

public class CommandResponse {
  private final double value;
  private final ResponseType responseType;
  private final String message;
  private long responseTime;

  private CommandResponse(double value) {
    this.responseType = ResponseType.OK;
    this.value = value;
    this.message = null;
  }

  private CommandResponse(Throwable exception) {
    this.responseType = ResponseType.ERR;
    this.value = 0;
    this.message = String.format("(%s) %s", exception.getClass().getSimpleName(), exception.getMessage());
  }

  public double getValue() {
    return value;
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

  public static CommandResponse error(Throwable exception) {
    return new CommandResponse(exception);
  }

  public String getMessage() {
    return this.message;
  }

  public static class Builder {
    private final ResponseType responseType;
    private double value = 0;

    Builder() {
      this.responseType = ResponseType.OK;
    }

    public static Builder ok() {
      return new Builder();
    }

    public Builder setValue(double value) {
      this.value=value;
      return this;
    }

    public CommandResponse build() {
      return new CommandResponse(value);
    }
  }
}
