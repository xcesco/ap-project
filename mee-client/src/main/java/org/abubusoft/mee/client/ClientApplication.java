package org.abubusoft.mee.client;


import org.apache.commons.io.FileUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

@Command(name = "MEE Client Application", mixinStandardHelpOptions = true, version = "1.0.0",
        description = "Generate somes calls to the specified MEE-server.")
public class ClientApplication implements Callable<Integer> {

  @Parameters(index = "0", description = "File containg commands")
  private File file;

  @Option(names = {"-s", "--server"}, description = "IP or name of the MEE server")
  private String host = "localhost";

  @Option(names = {"-p", "--port"}, description = "port used by the MEE server")
  private int port = 10_000;

  @Option(names = {"-d", "--delay"}, description = "wait time (ms) before client send another command (in the same connection)")
  private int delay = 0;

  @Option(names = {"-c", "--connections"}, description = "connections pool size (default=1).")
  private int connectionPoolSize = 1;

  @Option(names = {"-t", "--threads"}, description = "used threads (default=1).")
  private int threadPoolSize = 1;

  // this example implements Callable, so parsing, error handling and handling user
  // requests for usage help or version help can be done with one line of code.
  public static void main(String... args) {
    int exitCode = new CommandLine(new ClientApplication()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    System.out.println("Read file " + file.getAbsolutePath());
    List<String> lines = FileUtils.readLines(file, "UTF-8");
    ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
    System.out.println("Start thread pool " + threadPoolSize);
    IntStream.range(0, connectionPoolSize).forEach(job -> {
      pool.submit(() -> {
        System.out.println("Start job " + job + " on "+Thread.currentThread().getName());
        ClientConnection connection = new ClientConnection(job, host, port, lines, delay);
        connection.execute();
      });
    });

    shutdownAndAwaitTermination(pool);
    return 0;
  }

  /**
   * https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html
   *
   * @param pool
   */
  void shutdownAndAwaitTermination(ExecutorService pool) {
    pool.shutdown(); // Disable new tasks from being submitted
    try {
      // Wait a while for existing tasks to terminate
      if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
        pool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if (!pool.awaitTermination(60, TimeUnit.SECONDS))
          System.err.println("Pool did not terminate");
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      pool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
}
