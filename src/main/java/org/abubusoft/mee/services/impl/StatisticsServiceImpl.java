package org.abubusoft.mee.services.impl;

import org.abubusoft.mee.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StatisticsServiceImpl implements StatisticsService {
  private static final Logger logger = LoggerFactory
          .getLogger(StatisticsServiceImpl.class);
  private long maxExecutionTime;
  private long averageExecuteTime;
  private long minExecuteTime=Long.MAX_VALUE;
  private long commandCounter;

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

  /**
   *
   * <code>https://math.stackexchange.com/questions/22348/how-to-add-and-subtract-values-from-an-average</code>
   *
   * @param executionTime
   */
  @Override
  public void registryOperation(long executionTime) {
    maxExecutionTime = Math.max(maxExecutionTime, executionTime);
    minExecuteTime = Math.min(minExecuteTime, executionTime);
    commandCounter++;
    // AvgNew=AvgOld+(ValueNew-AvgOld)/SizeNew
    averageExecuteTime = averageExecuteTime + (executionTime - averageExecuteTime) / commandCounter;
    logger.info("Command execution time: average: {} ms,  minTime: {} ms, maxTime: {} ms, command counter: {}",
            averageExecuteTime, minExecuteTime, maxExecutionTime, commandCounter);
  }
}
