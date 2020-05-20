package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.commands.StatCommand;

public interface StatisticsService {
  CommandResponse compute(StatCommand command);

  void registryOperation(long operationTime);
}
