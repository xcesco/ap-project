/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.aop.LogExecutionTime;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.StatCommand;
import org.abubusoft.mee.server.services.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.abubusoft.mee.server.support.CommandResponseUtils.formatDuration;
import static org.abubusoft.mee.server.support.CommandResponseUtils.formatValue;

@Component
public class StatisticsServiceImpl implements StatisticsService {
  private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);
  private final ReadWriteLock rwl = new ReentrantReadWriteLock();
  private final Lock readLock = rwl.readLock();
  private final Lock writeLock = rwl.writeLock();
  /**
   * Max command execution time in milliseconds.
   */
  private long maxExecutionTime = Long.MIN_VALUE;
  /**
   * Average command execution time in milliseconds.
   */
  private long averageExecuteTime;
  /**
   * Min command execution time in milliseconds.
   */
  private long minExecuteTime = Long.MAX_VALUE;
  private long commandCounter;

  @LogExecutionTime
  @Override
  public CommandResponse execute(StatCommand command) {
    CommandResponse.Builder builder = CommandResponse.Builder.ok();
    readLock.lock();
    try {
      switch (command.getSubType()) {
        case REQS:
          builder.setValue(commandCounter);
          break;
        case AVG_TIME:
          builder.setValue(averageExecuteTime / 1000.0);
          break;
        case MIN_TIME:
          builder.setValue(minExecuteTime / 1000.0);
          break;
        case MAX_TIME:
          builder.setValue(maxExecutionTime / 1000.0);
          break;
      }
    } finally {
      readLock.unlock();
    }

    return builder.build();
  }

  /**
   * <code>https://math.stackexchange.com/questions/22348/how-to-add-and-subtract-values-from-an-average</code>
   */
  @Override
  public void recordCommandExecutionTime(long executionTime) {
    writeLock.lock();
    try {
      maxExecutionTime = Math.max(maxExecutionTime, executionTime);
      minExecuteTime = Math.min(minExecuteTime, executionTime);
      commandCounter++;
      // AvgNew=AvgOld+(ValueNew-AvgOld)/SizeNew
      averageExecuteTime = averageExecuteTime + (executionTime - averageExecuteTime) / commandCounter;
      logger.debug("Updated stats: average = {} s, min = {} s, max = {} s, counter = {}",
              formatValue(averageExecuteTime / 1_000.0),
              formatValue(minExecuteTime / 1_000.0),
              formatValue(maxExecutionTime / 1_000.0),
              commandCounter);
      logger.debug("Last commmand executed in {} s", formatDuration(executionTime));
    } finally {
      writeLock.unlock();
    }
  }
}
