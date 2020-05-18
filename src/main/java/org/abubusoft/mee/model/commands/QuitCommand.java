package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.CommandType;

public class QuitCommand extends AbstractCommand {
  public QuitCommand() {
    super(CommandType.BYE);
  }

  @Override
  public void execute() {

  }
}
