package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.services.ClientConnection;
import org.abubusoft.mee.server.services.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TcpServerImpl implements TcpServer, ClientConnection.Listener {
  private static final Logger logger = LoggerFactory
          .getLogger(TcpServerImpl.class);
  private final AtomicInteger clientConnectionCounter=new AtomicInteger(0);
  private Executor executor;
  private ObjectProvider<ClientConnection> clientConnectionProvider;

  @Autowired
  @Qualifier(ApplicationConfiguration.CONNECTION_EXECUTOR)
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  @Autowired
  public void setClientConnectionProvider(ObjectProvider<ClientConnection> clientConnectionProvider) {
    this.clientConnectionProvider = clientConnectionProvider;
  }

  @Override
  public void start(int port) {
    new Thread(() -> {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        logger.info("Server starts listening on TCP port {}", port);

        while (true) {
          try {
            ClientConnection clientConnection = clientConnectionProvider.getObject(serverSocket.accept(), this);
            executor.execute(clientConnection::start);
          } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
          }
        }

      } catch (IOException e) {
        logger.error("Could not open server on TCP port {}.", port);
        e.printStackTrace();
      }
    }).start();
  }

  @Override
  public void messageSent(ClientConnection clientConnection, String message, boolean error) {
    if (error) {
      logger.error(message);
    }
    logger.trace("Sent response '{}'", message);
  }

  @Override
  public void messageReceived(ClientConnection clientConnection, String message) {
    logger.trace("Received message '{}'", message);
  }

  @Override
  public synchronized void connected(ClientConnection clientConnection) {
    logger.info("New connection from {} ({} opened).", clientConnection.getAddress().getCanonicalHostName(), clientConnectionCounter.incrementAndGet());
  }

  @Override
  public synchronized void disconnected(ClientConnection clientConnection) {
    logger.info("Closed connection from {} ({} still opened).", clientConnection.getAddress().getCanonicalHostName(), clientConnectionCounter.decrementAndGet());
  }
}