package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.aop.LogExecutionTime;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.services.ComputeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ComputeServiceImpl implements ComputeService {
  private AsyncTaskExecutor executor;

  @Autowired
  @Qualifier(ApplicationConfiguration.COMPUTE_EXECUTOR)
  public void setExecutor(AsyncTaskExecutor executor) {
    this.executor = executor;
  }

  @LogExecutionTime
  @Override
  public CommandResponse execute(ComputeCommand command) {
    try {
      return executor.submit(command::execute).get();
    } catch (InterruptedException | ExecutionException e) {
      return CommandResponse.error(e.getCause());
    }
  }

}
