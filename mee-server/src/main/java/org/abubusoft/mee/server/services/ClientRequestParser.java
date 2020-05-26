package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.Command;

public interface ClientRequestParser {
  <E extends Command> E parse(String input);
}
