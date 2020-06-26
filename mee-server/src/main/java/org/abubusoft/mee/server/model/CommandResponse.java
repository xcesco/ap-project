/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
    private double value = 0;

    Builder() {
    }

    public static Builder ok() {
      return new Builder();
    }

    public Builder setValue(double value) {
      this.value = value;
      return this;
    }

    public CommandResponse build() {
      return new CommandResponse(value);
    }
  }
}
