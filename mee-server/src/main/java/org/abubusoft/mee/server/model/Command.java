package org.abubusoft.mee.server.model;

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
    final StringBuilder sb = new StringBuilder("Command{");
    sb.append("type=").append(type);
    sb.append('}');
    return sb.toString();
  }

  public CommandResponse execute() {
    return CommandResponse.Builder.create(ResponseType.OK, type).build();
  }

}
