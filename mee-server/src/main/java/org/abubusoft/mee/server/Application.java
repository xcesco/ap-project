package org.abubusoft.mee.server;

import org.abubusoft.mee.server.services.MeeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.abubusoft.mee.server"})
public class Application implements CommandLineRunner {
  private static Logger logger = LoggerFactory.getLogger(Application.class);

  @Value("${mee-server.port}")
  int serverPort;

  @Value("${mee-server.connections-in-queue}")
  int connectionsInQueue;

  private MeeServer meeServer;

  public static boolean isInteger(String string) {
    if (string == null) {
      return false;
    }
    try {
      Integer.parseInt(string);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }

  @Autowired
  public void setMeeServer(MeeServer meeServer) {
    this.meeServer = meeServer;
  }

  @Override
  public void run(String... args) {
    int port;

    if (args.length == 1 && isInteger(args[0])) {
      port = Integer.parseInt(args[0]);
      logger.info(String.format("Listening port %s is specified via command line args", port));
    } else {
      port = serverPort;
      logger.info(String.format("Listening port %s is specified via application config", port));
    }

    meeServer.start(port, connectionsInQueue);
  }

}
