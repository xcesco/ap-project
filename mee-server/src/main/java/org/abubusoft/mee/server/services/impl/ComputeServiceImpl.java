package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.commands.ComputeCommand;
import org.abubusoft.mee.server.services.ComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class ComputeServiceImpl implements ComputeService {

  private Executor executor;

  @Autowired
  @Qualifier(ApplicationConfiguration.COMMAND_EXECUTOR)
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  @LogExecutionTime
  @Override
  public CommandResponse compute(ComputeCommand command) {
    return CommandResponse.Builder.create(command.getType()).build();
  }
}
