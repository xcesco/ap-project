package org.abubusoft.mee.server;

import org.abubusoft.mee.server.services.ClientHandler;
import org.abubusoft.mee.server.services.impl.ClientHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Socket;
import java.util.concurrent.Executor;

@Configuration
public class ApplicationConfiguration {
  private static final Logger logger = LoggerFactory
          .getLogger(ApplicationConfiguration.class);
  public static final String CONNECTION_EXECUTOR = "connectionExecutor";
  public static final String COMPUTE_EXECUTOR = "computeExecutor";

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public ClientHandler clientConnection(Socket socket, ClientHandler.Listener listener) {
    return new ClientHandlerImpl(socket, listener);
  }

  @Bean(CONNECTION_EXECUTOR)
  public Executor connectionExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(256);
    executor.setQueueCapacity(0);
    executor.setThreadNamePrefix("Connection-");
    executor.initialize();
    return executor;
  }

  @Bean(COMPUTE_EXECUTOR)
  public AsyncTaskExecutor commandExecutor() {
    Runtime runtime = Runtime.getRuntime();
    int numberOfProcessors = runtime.availableProcessors();
    logger.info(String.format("%s max size is %d (available processors to this JVM)", COMPUTE_EXECUTOR, numberOfProcessors));
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(numberOfProcessors);
    executor.setMaxPoolSize(numberOfProcessors);
    executor.setThreadNamePrefix("Compute-");
    executor.initialize();
    return executor;
  }
}