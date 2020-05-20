package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.CommandType;

public abstract class Command {
  private final CommandType type;

  public Command(CommandType type) {
    this.type = type;
  }

  public CommandType getType() {
    return type;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("AbstractCommand{");
    sb.append("type=").append(type);
    sb.append('}');
    return sb.toString();
  }

  public abstract double execute();
}
