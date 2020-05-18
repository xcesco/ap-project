package org.abubusoft.mee.services;

import org.abubusoft.mee.model.Connection;

import java.util.List;

public interface ConnectionsServer {
  int getConnectionsCount();

  void start(int port);

  List<Connection> getConnections();

  void addListener(Connection.Listener listener);
}