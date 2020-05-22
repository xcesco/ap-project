package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.aop.LogExecutionTime;
import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
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
      if (e.getCause() instanceof AppRuntimeException) {
        AppRuntimeException cause = (AppRuntimeException) e.getCause();
        // error is already showed in log
        return propagateError(cause, false);
      } else {
        return propagateError(e, true);
      }
    }
  }

  private CommandResponse propagateError(Exception e, boolean showLog) {
    if (showLog) {
      logger.error(e.getMessage());
    }
    return CommandResponse.error(e);
  }

  private CommandResponse execute(ComputeCommand command) {
    return command.execute();
  }
}
