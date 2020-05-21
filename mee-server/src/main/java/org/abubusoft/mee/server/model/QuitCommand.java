package org.abubusoft.mee.server.model;

public class QuitCommand extends Command {
  public QuitCommand() {
    super(CommandType.BYE);
  }

  @Override
  public CommandResponse accept(CommandVisitor visitor) {
    return visitor.visit(this);
  }
}
