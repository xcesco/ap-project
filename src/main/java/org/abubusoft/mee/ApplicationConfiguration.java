package org.abubusoft.mee;

import org.abubusoft.mee.model.Connection;
import org.abubusoft.mee.model.connection.ConnectionImpl;
import org.abubusoft.mee.services.ConnectionsService;
import org.abubusoft.mee.services.impl.ConnectionsServiceImpl;
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

  @Bean
  public BiFunction<Socket, Connection.Listener, Connection> getConnectionFactory() {
    return this::createConnection;
  }

  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public Connection createConnection(Socket socket, Connection.Listener listener) {
    return new ConnectionImpl(socket, listener);
  }

  @Bean(name = "connectionExecutor")
  public Executor createConnectionExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setThreadNamePrefix("Connection-");
    executor.initialize();
    return executor;
  }

  @Bean(name = "commandExecutor")
  public Executor createCommandExecutor() {
    Runtime runtime = Runtime.getRuntime();
    int numberOfProcessors = runtime.availableProcessors();
    logger.info(String.format("commandExecutor max size is %d (number of processors available to this JVM)", numberOfProcessors));
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(numberOfProcessors);
    executor.setThreadNamePrefix("Command-");
    executor.initialize();
    return executor;
  }

  @Bean
  public ConnectionsService getConnectionServer() {
    return new ConnectionsServiceImpl();
  }
}