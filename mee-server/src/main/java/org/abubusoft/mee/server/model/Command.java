package org.abubusoft.mee.server.model;

import java.util.StringJoiner;

public abstract class Command implements CommandVisitable {
  private final CommandType type;

  public Command(CommandType type) {
    this.type = type;
  }

  public CommandType getType() {
    return type;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Command.class.getSimpleName() + "[", "]")
            .add("type=" + type)
            .toString();
  }

  public CommandResponse execute() {
    return CommandResponse.Builder.ok().build();
  }

}
