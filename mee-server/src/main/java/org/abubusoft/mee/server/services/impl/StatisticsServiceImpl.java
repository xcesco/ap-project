package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.commands.StatCommand;
import org.abubusoft.mee.server.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class StatisticsServiceImpl implements StatisticsService {
  private static final Logger logger = LoggerFactory
          .getLogger(StatisticsServiceImpl.class);
  private long maxExecutionTime;
  private long averageExecuteTime;
  private long minExecuteTime = Long.MAX_VALUE;
  private long commandCounter;
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock r = rwl.readLock();
  private final Lock w = rwl.writeLock();

  public long getMaxExecutionTime() {
    return maxExecutionTime;
  }

  public long getAverageExecuteTime() {
    return averageExecuteTime;
  }

  public long getMinExecuteTime() {
    return minExecuteTime;
  }

  public long getCommandCounter() {
    return commandCounter;
  }

  @LogExecutionTime
  @Override
  public CommandResponse compute(StatCommand command) {
    CommandResponse.Builder builder = CommandResponse.Builder.create(command.getType());
    r.lock();
    try {
      switch (command.getSubType()) {
        case REQS:
          builder.addValue(commandCounter);
          break;
        case AVG_TIME:
          builder.addValue(averageExecuteTime);
          break;
        case MAX_TIME:
          builder.addValue(maxExecutionTime);
          break;
      }
    } finally {
      r.unlock();
    }

    return builder.build();
  }

  /**
   * <code>https://math.stackexchange.com/questions/22348/how-to-add-and-subtract-values-from-an-average</code>
   *
   * @param executionTime
   */
  @Override
  public void registryOperation(long executionTime) {
    w.lock();
    try {
      maxExecutionTime = Math.max(maxExecutionTime, executionTime);
      minExecuteTime = Math.min(minExecuteTime, executionTime);
      commandCounter++;
      // AvgNew=AvgOld+(ValueNew-AvgOld)/SizeNew
      averageExecuteTime = averageExecuteTime + (executionTime - averageExecuteTime) / commandCounter;
//    logger.info("Command execution time: average: {} ms,  minTime: {} ms, maxTime: {} ms, command counter: {}",
//            averageExecuteTime, minExecuteTime, maxExecutionTime, commandCounter);
    } finally {
      w.unlock();
    }
  }
}
