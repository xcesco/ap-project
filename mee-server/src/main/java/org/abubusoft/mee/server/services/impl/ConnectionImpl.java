package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.*;
import org.abubusoft.mee.server.services.CommandParser;
import org.abubusoft.mee.server.services.ComputeService;
import org.abubusoft.mee.server.services.Connection;
import org.abubusoft.mee.server.services.StatisticsService;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionImpl implements Connection, CommandVisitor {
  private static final Logger logger = LoggerFactory
          .getLogger(ConnectionImpl.class);
  private final Socket socket;
  private final List<Listener> listeners = new ArrayList<>();
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
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  @Override
  public void start() {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));) {

      notifyConnectedEvent();

      Command command = null;
      CommandResponse response;
      while (command == null || CommandType.BYE != command.getType()) {
        String line = br.readLine();
        try {
          command = commandParser.parse(line);
          response = command.accept(this);

          logger.debug(command.toString());
          notifyReceivedCommandEvent(command);

          if (command.getType() != CommandType.BYE) {
            logger.debug(response.toString());
            sendResponse(bw, response);
          }
        } catch (MalformedCommandException e) {
          CommandResponse errorResponse = CommandResponse.error(e);
          sendResponse(bw, errorResponse);
          logger.error(CommandResponseUtils.format(errorResponse));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        logger.error(e.getMessage());
        //e.printStackTrace();
      }

      notifyDisconnetedEvent();
    }
  }

  private void sendResponse(BufferedWriter writer, CommandResponse response) throws IOException {
    writer.write(CommandResponseUtils.format(response) + System.lineSeparator());
    writer.flush();
  }

  private void notifyDisconnetedEvent() {
    for (Listener listener : listeners) {
      listener.disconnected(this);
    }
  }

  private void notifyReceivedCommandEvent(Command command) {
    for (Listener listener : listeners) {
      listener.messageReceived(this, command.getType());
    }
  }

  private void notifyConnectedEvent() {
    for (Listener listener : listeners) {
      listener.connected(this);
    }
  }


  @Override
  public CommandResponse visit(QuitCommand command) {
    return command.execute();

  }

  @Override
  public CommandResponse visit(ComputeCommand command) {
    return computeService.compute(command);
  }

  @Override
  public CommandResponse visit(StatCommand command) {
    return statisticsService.compute(command);

  }
}