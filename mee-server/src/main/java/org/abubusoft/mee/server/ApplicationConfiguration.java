package org.abubusoft.mee.server;

import org.abubusoft.mee.server.model.Connection;
import org.abubusoft.mee.server.model.connection.ConnectionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@Configuration
public class ApplicationConfiguration {

  private static final Logger logger = LoggerFactory
          .getLogger(ApplicationConfiguration.class);
  public static final String CONNECTION_EXECUTOR = "connectionExecutor";
  public static final String COMMAND_EXECUTOR = "commandExecutor";

  @Bean
  public BiFunction<Socket, Connection.Listener, Connection> getConnectionFactory() {
    return this::createConnection;
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public Connection createConnection(Socket socket, Connection.Listener listener) {
    return new ConnectionImpl(socket, listener);
  }

  @Bean(CONNECTION_EXECUTOR)
  public Executor connectionExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(256);
    executor.setThreadNamePrefix("Connection-");
    executor.initialize();
    return executor;
  }

  @Bean(COMMAND_EXECUTOR)
  public Executor commandExecutor() {
    Runtime runtime = Runtime.getRuntime();
    int numberOfProcessors = runtime.availableProcessors();
    logger.info(String.format("commandExecutor max size is %d (number of processors available to this JVM)", numberOfProcessors));
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //executor.setCorePoolSize(2);
    executor.setMaxPoolSize(numberOfProcessors);
    executor.setThreadNamePrefix("Command-");
    executor.initialize();
    return executor;
  }
}