package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.Connection;

import java.util.List;

public interface ConnectionsService {
  int getConnectionsCount();

  void start(int port);

  List<Connection> getConnections();

  void addListener(Connection.Listener listener);
}