package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.model.*;
import org.abubusoft.mee.server.services.ClientHandler;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.ComputeService;
import org.abubusoft.mee.server.services.StatisticsService;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientHandlerImpl implements ClientHandler, CommandVisitor {
  private static final Logger logger = LoggerFactory.getLogger(ClientHandlerImpl.class);
  private final Socket socket;
  private final Listener listener;
  private ClientRequestParser clientRequestParser;
  private StatisticsService statisticsService;
  private ComputeService computeService;

  public ClientHandlerImpl(Socket socket, Listener listener) {
    this.socket = socket;
    this.listener = listener;
  }

  @Autowired
  public void setClientRequestParser(ClientRequestParser clientRequestParser) {
    this.clientRequestParser = clientRequestParser;
  }

  @Autowired
  public void setStatisticsService(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Autowired
  public void setComputeService(ComputeService computeService) {
    this.computeService = computeService;
  }

  @Override
  public InetAddress getAddress() {
    return socket.getInetAddress();
  }

  @Override
  public void start() {
    notifyConnectedEvent();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

      Command command;
      do {
        String request = reader.readLine();
        command = handleRequest(writer, request);
      } while (CommandType.BYE != command.getType());
    } catch (IOException e) {
      logger.error(e.getMessage());
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        logger.error(e.getMessage());
      }

      notifyDisconnetedEvent();
    }
  }

  private Command handleRequest(BufferedWriter writer, String request) throws IOException {
    Command command = null;
    try {
      notifyReceivedCommandEvent(request);
      command = clientRequestParser.parse(request);
      CommandResponse response = execute(command);

      if (command.getType() != CommandType.BYE) {
        sendResponse(writer, response);
      }
    } catch (AppRuntimeException e) {
      CommandResponse errorResponse = CommandResponse.error(e);
      sendResponse(writer, errorResponse);
    }
    return command;
  }

  private CommandResponse execute(Command command) {
    return command.accept(this);
  }

  private void notifySentResponse(CommandResponse response) {
    String message = CommandResponseUtils.format(response);
    if (listener != null) {
      listener.messageSent(this, message, response.getResponseType() == ResponseType.ERR);
    }
  }

  private void sendResponse(BufferedWriter writer, CommandResponse response) throws IOException {
    writer.write(CommandResponseUtils.format(response) + System.lineSeparator());
    writer.flush();
    notifySentResponse(response);
  }

  private void notifyDisconnetedEvent() {
    if (listener != null) {
      listener.disconnected(this);
    }
  }

  private void notifyReceivedCommandEvent(String command) {
    if (listener != null) {
      listener.messageReceived(this, command);
    }
  }

  private void notifyConnectedEvent() {
    if (listener != null) {
      listener.connected(this);
    }
  }

  @Override
  public CommandResponse visit(QuitCommand command) {
    return command.execute();
  }

  @Override
  public CommandResponse visit(ComputeCommand command) {
    return computeService.execute(command);
  }

  @Override
  public CommandResponse visit(StatCommand command) {
    return statisticsService.execute(command);
  }
}