package org.abubusoft.mee.server.model;

public abstract class Command implements CommandVisitable {
  private final CommandType type;

  public Command(CommandType type) {
    this.type = type;
  }

  public CommandType getType() {
    return type;
  }

  public CommandResponse execute() {
    return CommandResponse.Builder.ok().build();
  }

}
