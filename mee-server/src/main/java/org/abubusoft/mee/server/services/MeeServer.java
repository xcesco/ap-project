package org.abubusoft.mee.server.services;

public interface MeeServer {
  void start(int port, int connectionsInQueue);
}