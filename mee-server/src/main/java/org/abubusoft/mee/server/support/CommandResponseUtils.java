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

package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ResponseType;

public abstract class CommandResponseUtils {
  private CommandResponseUtils() {
  }

  public static String format(CommandResponse response) {
    StringBuilder builder = new StringBuilder();

    builder.append(response.getResponseType())
            .append(";");

    if (response.getResponseType() == ResponseType.ERR) {
      builder.append(response.getMessage());
    } else {
      builder.append(formatDuration(response.getResponseTime()))
              .append(";")
              .append(formatValue(response.getValue()));
    }

    return builder.toString();
  }

  public static String formatDuration(long duration) {
    return String.format("%.3f", duration / 1_000.0);
  }

  public static String formatValue(double value) {
    return String.format("%.6f", value);
  }
}
