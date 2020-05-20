package org.abubusoft.mee.services.impl;

import org.abubusoft.mee.model.Connection;
import org.abubusoft.mee.services.ConnectionsServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionsServerImpl implements ConnectionsServer, Connection.Listener {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionsServerImpl.class);

  private final List<Connection> connections = new CopyOnWriteArrayList<>();
  private final List<Connection.Listener> listeners = new ArrayList<>();

  private ObjectProvider<Connection> connectionProvider;

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
            connection.start();
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