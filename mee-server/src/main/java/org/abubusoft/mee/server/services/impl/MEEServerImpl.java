package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.services.ClientHandler;
import org.abubusoft.mee.server.services.MEEServer;
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
public class MEEServerImpl implements MEEServer, ClientHandler.Listener {
  private static final Logger logger = LoggerFactory
          .getLogger(MEEServerImpl.class);
  private final AtomicInteger clientConnectionCounter = new AtomicInteger(0);
  private Executor executor;
  private ObjectProvider<ClientHandler> clientHandlerProvider;

  @Autowired
  @Qualifier(ApplicationConfiguration.CONNECTION_EXECUTOR)
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  @Autowired
  public void setClientHandlerProvider(ObjectProvider<ClientHandler> clientHandlerProvider) {
    this.clientHandlerProvider = clientHandlerProvider;
  }

  @Override
  public void start(int port) {
    new Thread(() -> {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        logger.info("Server starts listening on TCP port {}", port);

        while (true) {
          try {
            ClientHandler clientHandler = clientHandlerProvider.getObject(serverSocket.accept(), this);
            executor.execute(clientHandler::start);
          } catch (IOException e) {
            logger.error(e.getMessage());
          }
        }

      } catch (IOException e) {
        logger.error("Could not open server on TCP port {}. Reason: {}", port, e.getMessage());
      }
    }).start();
  }

  @Override
  public void messageSent(ClientHandler clientHandler, String request, boolean error) {
    if (error) {
      logger.error(request);
    }
    logger.trace("Sent response '{}'", request);
  }

  @Override
  public void messageReceived(ClientHandler clientHandler, String message) {
    logger.trace("Received message '{}'", message);
  }

  @Override
  public synchronized void connected(ClientHandler clientHandler) {
    logger.info("New connection from {} ({} opened).", clientHandler.getAddress().getCanonicalHostName(), clientConnectionCounter.incrementAndGet());
  }

  @Override
  public synchronized void disconnected(ClientHandler clientHandler) {
    logger.info("Closed connection from {} ({} still opened).", clientHandler.getAddress().getCanonicalHostName(), clientConnectionCounter.decrementAndGet());
  }
}