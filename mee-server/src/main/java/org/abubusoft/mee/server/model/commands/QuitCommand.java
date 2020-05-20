package org.abubusoft.mee.server.model.commands;

import org.abubusoft.mee.server.model.CommandType;

public class QuitCommand extends Command {
  public QuitCommand() {
    super(CommandType.BYE);
  }

}
