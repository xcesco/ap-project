package org.abubusoft.mee.server.services;

import java.net.InetAddress;

public interface ClientConnection {
  InetAddress getAddress();

  void start();

  interface Listener {

    void connected(ClientConnection clientConnection);

    void disconnected(ClientConnection clientConnection);

    void messageSent(ClientConnection clientConnection, String message, boolean error);

    void messageReceived(ClientConnection clientConnection, String message);
  }
}