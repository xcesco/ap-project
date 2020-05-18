package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.Command;
import org.abubusoft.mee.model.CommandType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.StringJoiner;

public abstract class AbstractCommand implements Command {
  private final CommandType type;

  public AbstractCommand(CommandType type) {
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

  public abstract void execute();
}
