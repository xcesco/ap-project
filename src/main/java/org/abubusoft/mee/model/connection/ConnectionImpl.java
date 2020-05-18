package org.abubusoft.mee.model.connection;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.model.Command;
import org.abubusoft.mee.model.CommandParser;
import org.abubusoft.mee.model.CommandType;
import org.abubusoft.mee.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ConnectionImpl implements Connection {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionImpl.class);
  private Socket socket;
  private List<Listener> listeners = new ArrayList<>();

  @Autowired
  @Qualifier("connectionExecutor")
  public void setExecutor(Executor executor) {
    this.executor = executor;
  }

  private Executor executor;

  @Autowired
  public ConnectionImpl setCommandParser(CommandParser commandParser) {
    this.commandParser = commandParser;
    return this;
  }

  private CommandParser commandParser;

  public ConnectionImpl(Socket socket, Listener listener) {
    this.socket = socket;
    this.listeners.add(listener);
  }

  @Override
  public InetAddress getAddress() {
    return socket.getInetAddress();
  }

  @Override
  public void send(Object objectToSend) {
//        if (objectToSend instanceof byte[]) {
//            byte[] data = (byte[]) objectToSend;
//            try {
//                outputStream.write(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
  }

  @Override
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  @Override
  public void start() {
    executor.execute(() -> {
      try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {

        for (Listener listener : listeners) {
          listener.connected(this);
        }

        Command command = null;
        while (command == null || CommandType.BYE != command.getType()) {
          String line = br.readLine();
          try {
            command = commandParser.parse(line);

            switch (command.getType()) {
              case STAT:
              case COMPUTE:
                logger.info(command.toString());
                for (Listener listener : listeners) {
                  listener.messageReceived(this, command.getType());
                }
              default:
                break;
            }
            bw.write("OK;" + System.lineSeparator());
            bw.flush();
          } catch (MalformedCommandException e) {
            String message = "Malformed command: " + e.getMessage();
            logger.error(message);
            bw.write("ERR:" + message + System.lineSeparator());
            bw.flush();
          }
        }
        bw.write("Quit connection " + System.lineSeparator());
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

        for (Listener listener : listeners) {
          listener.disconnected(this);
        }
      }
    });
  }
}