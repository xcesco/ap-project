package org.abubusoft.mee;

import org.abubusoft.mee.services.ConnectionsServer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(
        scanBasePackages = {"org.abubusoft.mee"})
public class Application implements CommandLineRunner {

  @Value("${mee-server.port}")
  String serverPort;

  private ConnectionsServer connectionsServer;

  @Autowired
  public void setConnectionsServer(ConnectionsServer connectionsServer) {
    this.connectionsServer = connectionsServer;
  }

  private static Logger logger = LoggerFactory
          .getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
  }

  @Override
  public void run(String... args) {
    int port;

    if (args.length == 1 && StringUtils.isNumeric(args[0])) {
      port = Integer.parseInt(args[0]);
      logger.info(String.format("Connection server port is %s (specified in command line args)", port));
    } else {
      port = Integer.parseInt(serverPort);
      logger.info(String.format("Connection server port is %s (specified in application config)", port));
    }

    connectionsServer.start(port);
  }

}
