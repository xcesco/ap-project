package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.model.commands.Command;

public interface CommandParser {
  /**
   * Convert an input string into a command. If input is not a valid command, an exception is throwed.
   *
   * @param input
   *    input string
   * @return
   *  {@link Command}
   * @throws MalformedCommandException
   */
  <E extends Command> E parse(String input) throws MalformedCommandException;
}
