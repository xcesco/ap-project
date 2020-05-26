package org.abubusoft.mee.client;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientConnection {
  public static final String BYE = "BYE";
  private final String serverAddress;
  private final int serverPort;
  private final List<String> commands;
  private final int delay;
  private final int jobId;

  public ClientConnection(int jobId, String serverAddress, int serverPort, List<String> commands, int delay) {
    this.serverAddress = serverAddress;
    this.serverPort = serverPort;
    this.commands = commands;
    this.delay = delay;
    this.jobId = jobId;
  }


  public void execute() {
    int localPort = 0;
    String localAddress = null;
    BufferedReader br;
    BufferedWriter writer;
    try (Socket socket = new Socket(serverAddress, serverPort)) {
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      localPort = socket.getLocalPort();
      localAddress = socket.getLocalAddress().toString();
      System.out.println(String.format("connect %s:%s to %s:%s", localAddress, localPort, serverAddress, serverPort));

      commands.forEach(command -> {
        try {
          if (isCommentLine(command)) {
            return;
          }
          sendMessage(jobId, writer, command);
          String response = br.readLine();
          System.out.println(String.format("JobId: %3d <-- RESP: " + response, jobId));

          if (delay > 0) {
            Thread.sleep(delay);
          }

        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      });

      sendMessage(jobId, writer, BYE);
    } catch (IOException e) {
      System.err.println(e);
    } finally {
      System.out.println(String.format("closing %s:%s to %s:%s", localAddress, localPort, serverAddress, serverPort));
    }
  }

  private boolean isCommentLine(String command) {
    return command.startsWith("#") || StringUtils.isAllBlank(command);
  }

  void sendMessage(int jobId, BufferedWriter writer, String messsage) throws IOException {
    writer.write(messsage + System.lineSeparator());
    writer.flush();
    System.out.println(String.format("JobId: %3d --> SEND: %s", jobId, messsage));
  }
}
