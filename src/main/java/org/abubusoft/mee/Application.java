package org.abubusoft.mee;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Application implements CommandLineRunner {

  private static Logger logger = LoggerFactory
          .getLogger(Application.class);

  public static void main(String[] args) {
    //showBanner();
    SpringApplication app = new SpringApplication(Application.class);
    app.run(args);
    logger.info("Stopping application");
  }

  private static void showBanner() throws IOException {
    System.out.println(getApplicationTitle().replace("${version}", ApplicationVersion.VERSION));
  }

  private static String getApplicationTitle() throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream("banner.txt");

    return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
  }

  @Override
  public void run(String... args) {
    logger.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      logger.info("args[{}]: {}", i, args[i]);
    }
  }
}
