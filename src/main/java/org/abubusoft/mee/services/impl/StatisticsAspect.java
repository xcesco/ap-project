package org.abubusoft.mee.services.impl;

import org.abubusoft.mee.services.StatisticsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    logger.info("{} executed in {} ms", joinPoint.getSignature(), executionTime);
    return proceed;
  }
}