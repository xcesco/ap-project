package org.abubusoft.mee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

  private static Logger logger = LoggerFactory
          .getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
    logger.info("Stopping application");
  }

  @Override
  public void run(String... args) {
    logger.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      logger.info("args[{}]: {}", i, args[i]);
    }
  }
}
