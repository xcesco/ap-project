package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.aop.LogExecutionTime;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.StatCommand;
import org.abubusoft.mee.server.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.abubusoft.mee.server.support.CommandResponseUtils.formatDuration;
import static org.abubusoft.mee.server.support.CommandResponseUtils.formatValue;

/**
 * Times is stored in milliseconds.
 */
@Component
public class StatisticsServiceImpl implements StatisticsService {
  private static final Logger logger = LoggerFactory
          .getLogger(StatisticsServiceImpl.class);
  private long maxExecutionTime = Long.MIN_VALUE;
  private long averageExecuteTime;
  private long minExecuteTime = Long.MAX_VALUE;
  private long commandCounter;
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock r = rwl.readLock();
  private final Lock w = rwl.writeLock();

  @LogExecutionTime
  @Override
  public CommandResponse compute(StatCommand command) {
    CommandResponse.Builder builder = CommandResponse.Builder.ok();
    r.lock();

    // return time in seconds
    switch (command.getSubType()) {
      case REQS:
        builder.addValue(commandCounter);
        break;
      case AVG_TIME:
        builder.addValue(averageExecuteTime / 1000.0);
        break;
      case MIN_TIME:
        builder.addValue(minExecuteTime / 1000.0);
        break;
      case MAX_TIME:
        builder.addValue(maxExecutionTime / 1000.0);
        break;
    }
    r.unlock();

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
      logger.debug("Stats: average = {} s,  min = {} s, max = {} s, counter = {}",
              formatValue(averageExecuteTime / 1_000.0),
              formatValue(minExecuteTime / 1_000.0),
              formatValue(maxExecutionTime / 1_000.0),
              commandCounter);
      logger.debug("Commmand executed in {} s", formatDuration(executionTime));

    } finally {
      w.unlock();
    }
  }
}
