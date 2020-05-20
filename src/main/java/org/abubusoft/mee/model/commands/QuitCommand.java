package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.CommandType;

public class QuitCommand extends Command {
  public QuitCommand() {
    super(CommandType.BYE);
  }

  @Override
  public void execute() {

  }
}
