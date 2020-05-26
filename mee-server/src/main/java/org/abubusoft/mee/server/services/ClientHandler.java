package org.abubusoft.mee.server.services;

import java.net.InetAddress;

public interface ClientHandler {
  InetAddress getAddress();

  void start();

  interface Listener {

    void connected(ClientHandler clientHandler);

    void disconnected(ClientHandler clientHandler);

    void messageSent(ClientHandler clientHandler, String request, boolean error);

    void messageReceived(ClientHandler clientHandler, String message);
  }
}