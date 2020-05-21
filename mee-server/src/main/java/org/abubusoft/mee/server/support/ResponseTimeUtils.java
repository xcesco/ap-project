package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.model.CommandResponse;

public abstract class ResponseTimeUtils {
  public static String formatResponseTime(long timeInMilliseconds) {
    return String.format("%.3f", timeInMilliseconds / 1000.0);
  }

  public static String format(CommandResponse response) {
    StringBuilder builder = new StringBuilder();
    builder.append("OK;")
            .append(formatResponseTime(response.getResponseTime()));

    response.getValues().forEach(item-> builder.append(";"+String.format("%.6f", item)));
    return builder.toString();
  }
}
