package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.commands.ComputeCommand;

public interface ComputeService {
  CommandResponse compute(ComputeCommand command);
}
