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
