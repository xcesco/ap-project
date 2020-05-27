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
  private static final Logger logger = LoggerFactory
          .getLogger(StatisticsAspect.class);

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  private StatisticsService statisticsService;

  @Around("@annotation(org.abubusoft.mee.server.aop.LogExecutionTime)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long executionTime = System.currentTimeMillis() - start;

    Command command = (Command) Arrays.stream(joinPoint.getArgs())
            .filter(item -> item instanceof Command).findFirst().orElse(null);
    if (command != null && proceed instanceof CommandResponse) {
      if (((CommandResponse) proceed).getResponseType() == ResponseType.OK) {
        statisticsService.registryCommandExecutionTime(executionTime);
        CommandResponse response = ((CommandResponse) proceed);
        response.setResponseTime(executionTime);
      } else {
        logger.debug(String.format("%s response is not registered in stats", ResponseType.ERR));
      }
    } else {
      logger.warn(String.format("%s.%s has wrong signature for stats registration", joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName()));
    }

    return proceed;
  }
}