package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.model.Connection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Service
public class ConnectionsServiceImpl implements ConnectionsService, Connection.Listener {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionsServiceImpl.class);
  private final List<Connection> connections = new CopyOnWriteArrayList<>();
  private final List<Connection.Listener> listeners = new ArrayList<>();

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
  public int getConnectionsCount() {
    return connections.size();
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
  public List<Connection> getConnections() {
    return connections;
  }

  @Override
  public void addListener(Connection.Listener listener) {
    listeners.add(listener);
  }

  @Override
  public void messageReceived(Connection connection, Object message) {
    logger.trace("Received new message from {}", connection.getAddress().getCanonicalHostName());
    logger.trace("Class name: {}, toString: {}", message.getClass().getCanonicalName(), message.toString());
    for (Connection.Listener listener : listeners) {
      listener.messageReceived(connection, message);
    }
  }

  @Override
  public void connected(Connection connection) {
    logger.info("New connection! IP: {}.", connection.getAddress().getCanonicalHostName());
    connections.add(connection);
    logger.info("Current connections count: {}", connections.size());
    for (Connection.Listener listener : listeners) {
      listener.connected(connection);
    }
  }

  @Override
  public void disconnected(Connection connection) {
    logger.info("Disconnect! IP: {}.", connection.getAddress().getCanonicalHostName());
    connections.remove(connection);
    logger.info("Current connections count: {}", connections.size());
    for (Connection.Listener listener : listeners) {
      listener.disconnected(connection);
    }
  }
}