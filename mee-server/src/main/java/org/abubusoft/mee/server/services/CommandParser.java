package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.Command;

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
