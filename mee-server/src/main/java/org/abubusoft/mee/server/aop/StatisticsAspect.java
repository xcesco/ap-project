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

package org.abubusoft.mee.server.aop;

import org.abubusoft.mee.server.model.Command;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.ResponseType;
import org.abubusoft.mee.server.services.StatisticsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class StatisticsAspect {
  private static final Logger logger = LoggerFactory.getLogger(StatisticsAspect.class);

  private StatisticsService statisticsService;

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Around("@annotation(org.abubusoft.mee.server.aop.LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long executionTime = System.currentTimeMillis() - start;

    Command command = (Command) Arrays.stream(joinPoint.getArgs())
            .filter(item -> item instanceof Command).findFirst().orElse(null);
    if (command != null && proceed instanceof CommandResponse) {
      if (ResponseType.OK == ((CommandResponse) proceed).getResponseType()) {
        statisticsService.recordCommandExecutionTime(executionTime);
        CommandResponse response = ((CommandResponse) proceed);
        response.setResponseTime(executionTime);
      } else {
        logger.debug("{} response is not registered in stats", ResponseType.ERR);
      }
    } else {
      logger.warn("{}.{} has wrong signature for stats registration", joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName());
    }

    return proceed;
  }
}