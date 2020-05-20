package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.commands.Command;
import org.abubusoft.mee.server.services.StatisticsService;
import org.abubusoft.mee.server.support.ResponseTimeUtils;
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
  private static final Logger logger = LoggerFactory
          .getLogger(StatisticsAspect.class);

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  private StatisticsService statisticsService;

  @Around("@annotation(LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    Object proceed = joinPoint.proceed();

    long executionTime = System.currentTimeMillis() - start;
    statisticsService.registryOperation(executionTime);

    String commandType = Arrays.stream(joinPoint.getArgs())
            .filter(item -> item instanceof Command).findFirst()
            .map(item -> ((Command) item).getType().toString())
            .orElse("<unknown>");

    if (proceed instanceof CommandResponse) {
      ((CommandResponse) proceed).setResponseTime(executionTime);
    }

    logger.info("{} executed in {} ms", commandType, ResponseTimeUtils.formatResponseTime(executionTime));
    return proceed;
  }
}