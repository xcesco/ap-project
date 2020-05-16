package org.abubusoft.mee.model;

public abstract class Command {
  public CommandType getType() {
    return type;
  }

  private CommandType type;

  public Command(CommandType type) {
    this.type = type;
  }

  public abstract void execute();
}
