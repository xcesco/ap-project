/*
 * Copyright 2020 Francesco Benincasa (info@abubusoft.com).
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.ApplicationConfiguration;
import org.abubusoft.mee.server.services.ClientHandler;
import org.abubusoft.mee.server.services.MeeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MeeServerImpl implements MeeServer, ClientHandler.Listener {
  private static final Logger logger = LoggerFactory.getLogger(MeeServerImpl.class);
  private final AtomicInteger clientConnectionCounter = new AtomicInteger(0);
  private final Executor executor;
  private final ObjectProvider<ClientHandler> clientHandlerProvider;

  @Value("${mee-server.connections-in-queue}")
  private int connectionsInQueue;

  @Autowired
  public MeeServerImpl(@Qualifier(ApplicationConfiguration.CONNECTION_EXECUTOR) Executor executor,
                       ObjectProvider<ClientHandler> clientHandlerProvider) {
    this.executor = executor;
    this.clientHandlerProvider = clientHandlerProvider;
  }

  @Override
  public void start(int port) {
    new Thread(null, () -> {
      try (ServerSocket serverSocket = new ServerSocket(port, connectionsInQueue)) {
        logger.info("Server starts listening on TCP port {}", port);

        while (true) {
          acceptConnection(serverSocket);
        }

      } catch (IOException | SecurityException | IllegalArgumentException e) {
        logger.error("Could not open server on TCP port {}. Reason: {}", port, e.getMessage());
      }
    }, "Server").start();
  }

  private void acceptConnection(ServerSocket serverSocket) {
    try {
      ClientHandler clientHandler = clientHandlerProvider.getObject(serverSocket.accept(), this);
      executor.execute(clientHandler::start);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void messageSent(ClientHandler clientHandler, String request, boolean error) {
    if (error) {
      logger.error(request);
    }
    logger.debug("Sent response: {}", request);
  }

  @Override
  public void messageReceived(ClientHandler clientHandler, String message) {
    logger.debug("Received request: {}", message);
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