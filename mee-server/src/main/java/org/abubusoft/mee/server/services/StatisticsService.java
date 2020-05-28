package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.StatCommand;

public interface StatisticsService {
  CommandResponse execute(StatCommand command);

  void recordCommandExecutionTime(long executionTime);
}
