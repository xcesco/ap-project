package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.Command;

public interface InputLineParser {
  <E extends Command> E parse(String input) throws MalformedCommandException;
}
