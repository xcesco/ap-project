package org.abubusoft.mee.server;

import org.abubusoft.mee.server.services.MeeServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.google.common.primitives.Ints.tryParse;

@SpringBootApplication(scanBasePackages = {"org.abubusoft.mee.server"})
public class Application implements CommandLineRunner {
  private static Logger logger = LoggerFactory.getLogger(Application.class);

  private MeeServer meeServer;

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
    if (args.length == 1) {
      Integer port = tryParse(args[0]);

      if (port != null && port > 0) {
        logger.info("Listening port {} is specified via command line args", port);
        meeServer.start(port);
      } else {
        logger.error("No valid listening port is specified via command line args");
      }
    } else {
      logger.error("Too many line args");
    }
  }

}
