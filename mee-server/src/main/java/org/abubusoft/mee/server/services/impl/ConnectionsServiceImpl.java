package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.services.Connection;
import org.abubusoft.mee.server.services.ConnectionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Service
public class ConnectionsServiceImpl implements ConnectionsService, Connection.Listener {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionsServiceImpl.class);
  private final List<Connection> connections = new CopyOnWriteArrayList<>();
  private Executor executor;
  private ObjectProvider<Connection> connectionProvider;

  @Autowired
  @Qualifier(ApplicationConfiguration.CONNECTION_EXECUTOR)
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  @Autowired
  public void setConnectionProvider(ObjectProvider<Connection> connectionProvider) {
    this.connectionProvider = connectionProvider;
  }

  @Override
  public void start(int port) {
    new Thread(() -> {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        logger.info("Server starts listening on port TCP port {}", port);

        while (true) {
          try {
            Socket socket = serverSocket.accept();
            Connection connection = connectionProvider.getObject(socket, this);
            executor.execute(connection::start);
          } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
          }
        }

      } catch (IOException e) {
        logger.error("Could not open server at TCP port {}.", port);
        e.printStackTrace();
      }
    }).start();
  }

  @Override
  public void messageSent(Connection connection, String message, boolean error) {
    if (error) {
      logger.error(message);
    }
    logger.trace("Sent response '{}'", message);
  }

  @Override
  public void messageReceived(Connection connection, String message) {
    logger.trace("Received command '{}'", message);
  }

  @Override
  public void connected(Connection connection) {
    logger.info("New connection from {}.", connection.getAddress().getCanonicalHostName());
    connections.add(connection);
    logger.debug("Current connections count: {}", connections.size());
  }

  @Override
  public void disconnected(Connection connection) {
    logger.info("Disconnect from {}.", connection.getAddress().getCanonicalHostName());
    connections.remove(connection);
    logger.debug("Current connections count is {}", connections.size());
  }
}