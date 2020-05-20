package org.abubusoft.mee.server.model.connection;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.CommandParser;
import org.abubusoft.mee.server.model.CommandResponse;
import org.abubusoft.mee.server.model.CommandType;
import org.abubusoft.mee.server.model.Connection;
import org.abubusoft.mee.server.model.commands.Command;
import org.abubusoft.mee.server.model.commands.ComputeCommand;
import org.abubusoft.mee.server.model.commands.StatCommand;
import org.abubusoft.mee.server.services.ComputeService;
import org.abubusoft.mee.server.services.StatisticsService;
import org.abubusoft.mee.server.support.ResponseTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionImpl implements Connection {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionImpl.class);
  private Socket socket;
  private List<Listener> listeners = new ArrayList<>();
  private CommandParser commandParser;
  private StatisticsService statisticsService;
  private ComputeService computeService;

  public ConnectionImpl(Socket socket, Listener listener) {
    this.socket = socket;
    this.listeners.add(listener);
  }

  @Autowired
  public void setCommandParser(CommandParser commandParser) {
    this.commandParser = commandParser;
  }

  @Autowired
  public ConnectionImpl setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
    return this;
  }

  @Autowired
  public ConnectionImpl setComputeService(ComputeService computeService) {
    this.computeService = computeService;
    return this;
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
    try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {

      for (Listener listener : listeners) {
        listener.connected(this);
      }

      Command command = null;
      CommandResponse response = null;
      while (command == null || CommandType.BYE != command.getType()) {
        String line = br.readLine();
        try {
          command = commandParser.parse(line);
          switch (command.getType()) {
            case STAT:
              response = execute((StatCommand) command);
              break;
            case COMPUTE:
              response = execute((ComputeCommand) command);
              break;
            case BYE:
            default:
              break;
          }

          logger.info(command.toString());

          for (Listener listener : listeners) {
            listener.messageReceived(this, command.getType());
          }

          bw.write(ResponseTimeUtils.format(response));
          bw.flush();
        } catch (MalformedCommandException e) {
          String message = "Invalid command: " + e.getMessage();
          logger.error(message);
          bw.write("ERR:" + message + System.lineSeparator());
          bw.flush();
        }
      }
      //bw.write("Quit connection " + System.lineSeparator());
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
  }

  CommandResponse execute(ComputeCommand command) {
    return this.computeService.compute(command);
  }

  CommandResponse execute(StatCommand command) {
    return statisticsService.compute(command);
  }
}