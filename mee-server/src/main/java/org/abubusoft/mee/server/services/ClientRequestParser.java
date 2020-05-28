package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.Command;

public interface ClientRequestParser {
  Command parse(String input);
}
