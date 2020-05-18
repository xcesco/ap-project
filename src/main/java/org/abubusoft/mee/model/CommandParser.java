package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;

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
  Command parse(String input) throws MalformedCommandException;
}
