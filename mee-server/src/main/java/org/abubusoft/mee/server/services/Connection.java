package org.abubusoft.mee.server.services;

import java.net.InetAddress;

public interface Connection {
  InetAddress getAddress();

  void addListener(Listener listener);

  void start();

  interface Listener {

    void connected(Connection connection);

    void disconnected(Connection connection);

    void messageSent(Connection connection, String message, boolean error);

    void messageReceived(Connection connection, String message);
  }
}