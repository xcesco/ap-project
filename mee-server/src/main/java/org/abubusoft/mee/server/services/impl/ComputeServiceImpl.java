package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.ResponseType;
import org.abubusoft.mee.server.services.ComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class ComputeServiceImpl implements ComputeService {
  private static final Logger logger = LoggerFactory
          .getLogger(ComputeServiceImpl.class);

  private AsyncTaskExecutor executor;

  @Autowired
  @Qualifier(ApplicationConfiguration.COMMAND_EXECUTOR)
  public void setExecutor(AsyncTaskExecutor executor) {
    this.executor = executor;
  }

  @LogExecutionTime
  @Override
  public CommandResponse compute(ComputeCommand command) {
    Future<CommandResponse> result = executor.submit(() -> execute(command));
    try {
      return result.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return CommandResponse.Builder.create(ResponseType.ERR, command.getType()).build();
    }
  }

  private CommandResponse execute(ComputeCommand command) {
    logger.debug("executing " + command.getExpressionsList());
    return command.execute();
  }
}
